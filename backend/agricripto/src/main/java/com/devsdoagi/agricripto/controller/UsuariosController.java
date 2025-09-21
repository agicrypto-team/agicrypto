package com.devsdoagi.agricripto.controller;

import com.devsdoagi.agricripto.service.UsuariosService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuariosController {

    private final UsuariosService usuariosService;

    public UsuariosController(UsuariosService usuariosService) { this.usuariosService = usuariosService; }

    @GetMapping("/listar")
    public List<com.devsdoagi.agricripto.model.Usuarios> listar() { return  usuariosService.listarTodos(); }

}
