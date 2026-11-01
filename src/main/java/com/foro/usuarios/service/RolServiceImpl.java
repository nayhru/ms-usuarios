package com.foro.usuarios.service;

import com.foro.usuarios.dto.RolRequest;
import com.foro.usuarios.exception.ConflictoException;
import com.foro.usuarios.exception.RecursoNoEncontradoException;
import com.foro.usuarios.model.Rol;
import com.foro.usuarios.repository.RolRepository;
import com.foro.usuarios.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class RolServiceImpl implements RolService {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<Rol> getAllRoles() {
        return rolRepository.findAll();
    }

    @Override
    public Rol getRolById(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el rol con id " + id));
    }

    @Override
    @Transactional
    public Rol createRol(RolRequest datos) {
        if (rolRepository.existsByNombre(datos.nombre())) {
            throw new ConflictoException("Ya existe el rol " + datos.nombre());
        }
        Rol rol = new Rol();
        rol.setNombre(datos.nombre());
        rol.setDescripcion(datos.descripcion());
        Rol guardado = rolRepository.save(rol);
        log.info("Rol creado: id={}, nombre={}", guardado.getId(), guardado.getNombre());
        return guardado;
    }

    @Override
    @Transactional
    public Rol updateRol(Long id, RolRequest datos) {
        Rol rol = getRolById(id);
        if (rolRepository.existsByNombreAndIdNot(datos.nombre(), id)) {
            throw new ConflictoException("Ya existe el rol " + datos.nombre());
        }
        rol.setNombre(datos.nombre());
        rol.setDescripcion(datos.descripcion());
        log.info("Rol actualizado: id={}, nombre={}", id, datos.nombre());
        return rol;
    }

    @Override
    @Transactional
    public void deleteRol(Long id) {
        Rol rol = getRolById(id);
        if (usuarioRepository.existsByRolId(id)) {
            throw new ConflictoException("No se puede eliminar el rol " + rol.getNombre()
                    + " porque tiene usuarios asignados");
        }
        rolRepository.delete(rol);
        log.info("Rol eliminado: id={}, nombre={}", id, rol.getNombre());
    }
}
