package com.foro.usuarios.service;

import com.foro.usuarios.dto.UsuarioRequest;
import com.foro.usuarios.dto.UsuarioResponse;
import com.foro.usuarios.dto.UsuarioUpdateRequest;
import com.foro.usuarios.exception.AccesoDenegadoException;
import com.foro.usuarios.exception.ConflictoException;
import com.foro.usuarios.exception.RecursoNoEncontradoException;
import com.foro.usuarios.model.Rol;
import com.foro.usuarios.model.Usuario;
import com.foro.usuarios.repository.RolRepository;
import com.foro.usuarios.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final String ROL_ADMIN = "ADMIN";
    private static final String ROL_POR_DEFECTO = "USUARIO";

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> getAllUsuarios(String rol) {
        List<Usuario> usuarios = (rol == null || rol.isBlank())
                ? usuarioRepository.findAllByOrderByIdAsc()
                : usuarioRepository.findByRolNombreOrderByIdAsc(rol.toUpperCase());
        return usuarios.stream().map(UsuarioResponse::desde).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse getUsuarioById(Long id, Jwt solicitante) {
        validarDuenoOAdmin(id, solicitante);
        return UsuarioResponse.desde(buscarUsuario(id));
    }

    @Override
    @Transactional
    public UsuarioResponse createUsuario(UsuarioRequest datos, Jwt solicitante) {
        if (datos.rolId() != null && !esAdmin(solicitante)) {
            throw new AccesoDenegadoException("Solo un ADMIN puede asignar un rol al crear un usuario");
        }
        if (usuarioRepository.existsByNombreUsuario(datos.nombreUsuario())) {
            throw new ConflictoException("El nombre de usuario " + datos.nombreUsuario() + " ya esta en uso");
        }
        if (usuarioRepository.existsByCorreo(datos.correo())) {
            throw new ConflictoException("El correo " + datos.correo() + " ya esta registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(datos.nombreUsuario());
        usuario.setCorreo(datos.correo());
        usuario.setClaveHash(passwordEncoder.encode(datos.clave()));
        usuario.setNombreCompleto(datos.nombreCompleto());
        usuario.setRol(datos.rolId() != null ? buscarRol(datos.rolId()) : rolPorDefecto());

        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario creado: id={}, nombreUsuario={}, rol={}",
                guardado.getId(), guardado.getNombreUsuario(), guardado.getRol().getNombre());
        return UsuarioResponse.desde(guardado);
    }

    @Override
    @Transactional
    public UsuarioResponse updateUsuario(Long id, UsuarioUpdateRequest datos, Jwt solicitante) {
        validarDuenoOAdmin(id, solicitante);
        boolean cambiaPrivilegios = datos.rolId() != null || datos.activo() != null;
        if (cambiaPrivilegios && !esAdmin(solicitante)) {
            throw new AccesoDenegadoException("Solo un ADMIN puede cambiar el rol o el estado de un usuario");
        }

        Usuario usuario = buscarUsuario(id);
        if (usuarioRepository.existsByCorreoAndIdNot(datos.correo(), id)) {
            throw new ConflictoException("El correo " + datos.correo() + " ya esta registrado");
        }

        usuario.setCorreo(datos.correo());
        usuario.setNombreCompleto(datos.nombreCompleto());
        if (datos.clave() != null) {
            usuario.setClaveHash(passwordEncoder.encode(datos.clave()));
        }
        if (datos.rolId() != null) {
            usuario.setRol(buscarRol(datos.rolId()));
        }
        if (datos.activo() != null) {
            usuario.setActivo(datos.activo());
        }
        log.info("Usuario actualizado: id={}, por={}, rol={}, activo={}",
                id, solicitante.getSubject(), usuario.getRol().getNombre(), usuario.getActivo());
        return UsuarioResponse.desde(usuario);
    }

    @Override
    @Transactional
    public void deleteUsuario(Long id, Jwt solicitante) {
        validarDuenoOAdmin(id, solicitante);
        Usuario usuario = buscarUsuario(id);
        usuarioRepository.delete(usuario);
        log.info("Usuario eliminado: id={}, nombreUsuario={}, por={}",
                id, usuario.getNombreUsuario(), solicitante.getSubject());
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario con id " + id));
    }

    private Rol buscarRol(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el rol con id " + id));
    }

    private Rol rolPorDefecto() {
        return rolRepository.findByNombre(ROL_POR_DEFECTO)
                .orElseThrow(() -> new IllegalStateException("Falta el rol " + ROL_POR_DEFECTO + " en la base de datos"));
    }

    private boolean esAdmin(Jwt solicitante) {
        return solicitante != null && ROL_ADMIN.equals(solicitante.getClaimAsString("rol"));
    }

    private void validarDuenoOAdmin(Long id, Jwt solicitante) {
        Long idSolicitante = ((Number) solicitante.getClaim("uid")).longValue();
        if (!id.equals(idSolicitante) && !esAdmin(solicitante)) {
            throw new AccesoDenegadoException("Solo puedes operar sobre tu propia cuenta");
        }
    }
}
