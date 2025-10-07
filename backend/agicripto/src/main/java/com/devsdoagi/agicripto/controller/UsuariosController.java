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

    // login já existente (você já tem algo similar)
    @PostMapping("/login")
    public ResponseEntity<UsuariosResponseDTO> login(@RequestBody LoginRequestDTO loginRequest, HttpSession session) {
        Usuarios usuario = usuarioService.autenticar(loginRequest);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        session.setAttribute("USUARIO_ID", usuario.getId());
        session.setAttribute("TIPO_USUARIO", usuario.getTipo());
        session.setAttribute("LOGADO", true);

        return ResponseEntity.ok(new UsuariosResponseDTO(usuario));
    }

    @GetMapping("/sessao")
    public ResponseEntity<UsuariosResponseDTO> sessao(HttpSession session) {
        Integer id = (Integer) session.getAttribute("USUARIO_ID");
        String tipo = (String) session.getAttribute("TIPO_USUARIO");

        if (id == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuarios usuario = usuarioService.buscarUsuarioPorId(id);
        return ResponseEntity.ok(new UsuariosResponseDTO(usuario));
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
