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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private static Jwt token(long id, String rol) {
        return Jwt.withTokenValue("token").header("alg", "HS256")
                .subject("usuario" + id).claim("uid", id).claim("rol", rol).build();
    }

    private static Rol rol(String nombre) {
        Rol rol = new Rol();
        rol.setNombre(nombre);
        return rol;
    }

    @Test
    void registroPublicoAsignaRolUsuarioYCifraLaClave() {
        UsuarioRequest datos = new UsuarioRequest("nuevo", "nuevo@foro.cl", "Clave2026*", "Nuevo Usuario", null);
        when(rolRepository.findByNombre("USUARIO")).thenReturn(Optional.of(rol("USUARIO")));
        when(passwordEncoder.encode("Clave2026*")).thenReturn("hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(llamada -> llamada.getArgument(0));

        UsuarioResponse creado = usuarioService.createUsuario(datos, null);

        assertEquals("USUARIO", creado.rol());
        verify(passwordEncoder).encode("Clave2026*");
    }

    @Test
    void noPermiteAsignarRolSinSerAdmin() {
        UsuarioRequest datos = new UsuarioRequest("nuevo", "nuevo@foro.cl", "Clave2026*", "Nuevo Usuario", 1L);

        assertThrows(AccesoDenegadoException.class, () -> usuarioService.createUsuario(datos, token(3, "USUARIO")));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void rechazaNombreDeUsuarioDuplicado() {
        UsuarioRequest datos = new UsuarioRequest("admin", "otro@foro.cl", "Clave2026*", "Otro", null);
        when(usuarioRepository.existsByNombreUsuario("admin")).thenReturn(true);

        assertThrows(ConflictoException.class, () -> usuarioService.createUsuario(datos, null));
    }

    @Test
    void usuarioNoPuedeVerCuentaAjena() {
        assertThrows(AccesoDenegadoException.class, () -> usuarioService.getUsuarioById(1L, token(3, "USUARIO")));
    }

    @Test
    void usuarioNoPuedeCambiarSuPropioRol() {
        UsuarioUpdateRequest datos = new UsuarioUpdateRequest("yo@foro.cl", "Yo", null, 1L, null);

        assertThrows(AccesoDenegadoException.class, () -> usuarioService.updateUsuario(3L, datos, token(3, "USUARIO")));
    }

    @Test
    void adminRecibeErrorSiElUsuarioNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> usuarioService.getUsuarioById(99L, token(1, "ADMIN")));
    }
}
