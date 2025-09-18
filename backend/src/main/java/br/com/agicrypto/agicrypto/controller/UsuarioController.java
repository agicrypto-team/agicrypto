package br.com.agicrypto.agicrypto.controller;

import br.com.agicrypto.agicrypto.model.Usuarios;
import br.com.agicrypto.agicrypto.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioRepository repository;

    public UsuarioController(UsuarioRepository repository) { this.repository = repository; }

    @GetMapping("/listar")
    public List<Usuarios> listar() { return  repository.findAll(); }

}
