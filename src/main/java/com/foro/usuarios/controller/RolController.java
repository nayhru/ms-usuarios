package com.foro.usuarios.controller;

import com.foro.usuarios.dto.RolRequest;
import com.foro.usuarios.model.Rol;
import com.foro.usuarios.service.RolService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping
    public List<Rol> getAllRoles() {
        return rolService.getAllRoles();
    }

    @GetMapping("/{id}")
    public Rol getRolById(@PathVariable Long id) {
        return rolService.getRolById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Rol createRol(@Valid @RequestBody RolRequest rol) {
        return rolService.createRol(rol);
    }

    @PutMapping("/{id}")
    public Rol updateRol(@PathVariable Long id, @Valid @RequestBody RolRequest rol) {
        return rolService.updateRol(id, rol);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRol(@PathVariable Long id) {
        rolService.deleteRol(id);
    }
}
