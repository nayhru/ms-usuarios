package com.foro.usuarios.repository;

import com.foro.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @EntityGraph(attributePaths = "rol")
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    @EntityGraph(attributePaths = "rol")
    List<Usuario> findAllByOrderByIdAsc();

    @EntityGraph(attributePaths = "rol")
    List<Usuario> findByRolNombreOrderByIdAsc(String nombreRol);

    boolean existsByNombreUsuario(String nombreUsuario);

    boolean existsByCorreo(String correo);

    boolean existsByCorreoAndIdNot(String correo, Long id);

    boolean existsByRolId(Long rolId);
}
