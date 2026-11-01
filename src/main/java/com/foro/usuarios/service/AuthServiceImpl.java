package com.foro.usuarios.service;

import com.foro.usuarios.dto.LoginRequest;
import com.foro.usuarios.dto.LoginResponse;
import com.foro.usuarios.exception.CredencialesInvalidasException;
import com.foro.usuarios.model.Usuario;
import com.foro.usuarios.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Value("${app.jwt.expiracion-minutos}")
    private long expiracionMinutos;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest credenciales) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(credenciales.nombreUsuario())
                .filter(encontrado -> passwordEncoder.matches(credenciales.clave(), encontrado.getClaveHash()))
                .orElseThrow(() -> {
                    log.warn("Inicio de sesion rechazado para '{}': credenciales invalidas", credenciales.nombreUsuario());
                    return new CredencialesInvalidasException("Usuario o clave incorrectos");
                });

        if (!usuario.getActivo()) {
            log.warn("Inicio de sesion rechazado para '{}': cuenta desactivada", usuario.getNombreUsuario());
            throw new CredencialesInvalidasException("La cuenta esta desactivada");
        }

        String rol = usuario.getRol().getNombre();
        log.info("Inicio de sesion correcto: id={}, nombreUsuario={}, rol={}", usuario.getId(), usuario.getNombreUsuario(), rol);
        return new LoginResponse(generarToken(usuario, rol), "Bearer", expiracionMinutos,
                usuario.getId(), usuario.getNombreUsuario(), rol);
    }

    private String generarToken(Usuario usuario, String rol) {
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("ms-usuarios")
                .issuedAt(ahora)
                .expiresAt(ahora.plus(expiracionMinutos, ChronoUnit.MINUTES))
                .subject(usuario.getNombreUsuario())
                .claim("uid", usuario.getId())
                .claim("rol", rol)
                .build();
        JwsHeader cabecera = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(cabecera, claims)).getTokenValue();
    }
}
