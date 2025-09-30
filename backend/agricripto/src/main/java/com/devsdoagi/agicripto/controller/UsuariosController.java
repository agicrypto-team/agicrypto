package com.devsdoagi.agicripto.controller;

import com.devsdoagi.agicripto.model.Usuarios;
import com.devsdoagi.agicripto.DTO.UsuariosResponseDTO;

import com.devsdoagi.agicripto.service.UsuariosService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuariosController {

    private final UsuariosService usuarioService;

    public UsuariosController(UsuariosService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Cadastra novo usuário
    @PostMapping
    public ResponseEntity<UsuariosResponseDTO> cadastrarUsuario(@Valid @RequestBody Usuarios usuario) {

        usuarioService.cadastrar(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UsuariosResponseDTO(usuario));

    }

    // Lista todos os clientes (rota para uso interno/admin)
    @GetMapping("/clientes")
    public ResponseEntity<List<UsuariosResponseDTO>> listarClientes() {

        List<UsuariosResponseDTO> listaClientesDTO = usuarioService.listarClientes().stream().map(UsuariosResponseDTO::new).toList();
        return ResponseEntity.ok(listaClientesDTO);

    }

    // Lista todos os administradores (rota para uso interno/admin)
    @GetMapping("/admins")
    public ResponseEntity<List<UsuariosResponseDTO>> listarAdmins() {

        List<UsuariosResponseDTO> listaAdminsDTO = usuarioService.listarAdmins().stream().map(UsuariosResponseDTO::new).toList();
        return ResponseEntity.ok(listaAdminsDTO);

    }
    /*
    // Atualiza um usuário existente
    // A melhor forma é atualizar pelo id?
    @PutMapping("/{id}")
    public ResponseEntity<Usuarios> atualizarUsuario(@PathVariable Integer id, @Valid @RequestBody Usuarios usuario) {}

    // Busca um usuário por ID
    // A melhor forma é buscar pelo id?
    @GetMapping("/{id}")
    public ResponseEntity<Usuarios> buscarUsuarioPorId(@PathVariable Integer id) {}

    // Deleta um usuário
    // A melhor forma é deletar pelo id?
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Integer id) {}

    // Autentica um usuário
    @PostMapping("/login")
    public ResponseEntity<?> autenticarUsuario(@RequestBody LoginRequest loginRequest) {}


    */
}
