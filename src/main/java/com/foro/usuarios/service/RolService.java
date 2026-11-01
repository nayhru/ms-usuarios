package com.foro.usuarios.service;

import com.foro.usuarios.dto.RolRequest;
import com.foro.usuarios.model.Rol;

import java.util.List;

public interface RolService {

    List<Rol> getAllRoles();

    Rol getRolById(Long id);

    Rol createRol(RolRequest rol);

    Rol updateRol(Long id, RolRequest rol);

    void deleteRol(Long id);
}
