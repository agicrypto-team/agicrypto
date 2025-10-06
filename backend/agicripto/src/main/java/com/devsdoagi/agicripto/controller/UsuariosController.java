package com.devsdoagi.agicripto.controller;

import com.devsdoagi.agicripto.DTO.usuarios.CadastroUsuariosRequestDTO;
import com.devsdoagi.agicripto.DTO.usuarios.LoginRequestDTO;
import com.devsdoagi.agicripto.exception.usuarios.AutenticacaoException;
import com.devsdoagi.agicripto.model.Usuarios;
import com.devsdoagi.agicripto.DTO.usuarios.UsuariosResponseDTO;
import com.devsdoagi.agicripto.service.UsuariosService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuariosController {

    private final UsuariosService usuarioService;

    public UsuariosController(UsuariosService usuarioService) {

        this.usuarioService = usuarioService;

    }
    // Metodo privado (para uso somente na própria classe UsuariosController) utilitário para checar a validade da sessão do usuário, e obter o seu ID
    // Este metodo que garante que as requisições que necessitam de o usuário estar logado sejam sucedidas somente se os usuários estiverem de fato devidamente logados
    private Integer checarSessaoEObterIdUsuario(HttpSession session) {

        if (session == null || session.getAttribute("LOGADO") == null || !(Boolean) session.getAttribute("LOGADO")) {

            throw new AutenticacaoException("Acesso negado. Usuário não autenticado ou sessão expirada.");

        }

        return (Integer) session.getAttribute("USUARIO_ID");

    }

    // Cadastra novo cliente
    @PostMapping("/cliente")
    public ResponseEntity<UsuariosResponseDTO> cadastrarCliente(@Valid @RequestBody CadastroUsuariosRequestDTO usuario) {

        Usuarios usuarioCadastrado = usuarioService.cadastrarCliente(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UsuariosResponseDTO(usuarioCadastrado));

    }

    // Cadastra novo admin
    @PostMapping("/admin")
    public ResponseEntity<UsuariosResponseDTO> cadastrarAdmin(@Valid @RequestBody CadastroUsuariosRequestDTO usuario) {

        Usuarios usuarioCadastrado = usuarioService.cadastrarAdmin(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UsuariosResponseDTO(usuarioCadastrado));

    }

    // Autentica um usuário
    @PostMapping("/login")
    public ResponseEntity<UsuariosResponseDTO> login(@RequestBody LoginRequestDTO loginRequest, HttpSession session) {

        Usuarios usuarioAutenticado = usuarioService.autenticar(loginRequest);

        session.setAttribute("USUARIO_ID", usuarioAutenticado.getId());
        session.setAttribute("LOGADO", true);

        return ResponseEntity.ok(new UsuariosResponseDTO(usuarioAutenticado));

    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {

        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    // Lista todos os clientes (rota para uso interno/admin)
    @GetMapping("/clientes")
    public ResponseEntity<List<UsuariosResponseDTO>> listarClientes(HttpSession session) {


        Integer userId = checarSessaoEObterIdUsuario(session);
        usuarioService.validarUsuarioAdmin(userId);
        List<UsuariosResponseDTO> listaClientesDTO = usuarioService.listarClientes().stream().map(UsuariosResponseDTO::new).toList();
        return ResponseEntity.ok(listaClientesDTO);

    }

    // Lista todos os administradores (rota para uso interno/admin)
    @GetMapping("/admins")
    public ResponseEntity<List<UsuariosResponseDTO>> listarAdmins(HttpSession session) {

        Integer userId = checarSessaoEObterIdUsuario(session);
        usuarioService.validarUsuarioAdmin(userId);
        List<UsuariosResponseDTO> listaAdminsDTO = usuarioService.listarAdmins().stream().map(UsuariosResponseDTO::new).toList();
        return ResponseEntity.ok(listaAdminsDTO);

    }


    // Cliente deleta o seu próprio perfil
    @DeleteMapping("/cliente/me")
    public ResponseEntity<Void> autoDeletarPerfilCliente(HttpSession session) {

        Integer userId = checarSessaoEObterIdUsuario(session);
        usuarioService.autoDeletarPerfilCliente(userId);
        session.invalidate();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    /*
    // Admin deleta o perfil do cliente
    @DeleteMapping("/cliente")
    public ResponseEntity<Void> adminDeletarCliente(HttpSession session) {}
    */

    @GetMapping("/me")
    public ResponseEntity<UsuariosResponseDTO> obterDadosUsuario(HttpSession session) {

        Integer userId = checarSessaoEObterIdUsuario(session);
        Usuarios usuario = usuarioService.buscarUsuarioPorId(userId);

        return ResponseEntity.ok(new UsuariosResponseDTO(usuario));

    }




}
