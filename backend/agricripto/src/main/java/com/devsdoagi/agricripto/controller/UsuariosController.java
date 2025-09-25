package com.devsdoagi.agricripto.controller;

import com.devsdoagi.agricripto.repository.UsuariosRepository;
import com.devsdoagi.agricripto.model.Usuarios;riosRepository;
import com.devsdoagi.agricripto.DTO.UsuariosResponseDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuariosController {

    @Autowired
    private final UsuariosService usuarioService;

    // Cadastra novo usuário
    @PostMapping
    public ResponseEntity<UsuariosResponseDTO> cadastrarUsuario(@Valid @RequestBody Usuarios usuario) {

        usuarioService.cadastrar(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioResponseDTO(usuario));

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
