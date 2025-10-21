package com.devsdoagi.agicripto.service;

import com.devsdoagi.agicripto.DTO.usuarios.CadastroUsuariosRequestDTO;
import com.devsdoagi.agicripto.DTO.usuarios.LoginRequestDTO;
import com.devsdoagi.agicripto.exception.usuarios.AutenticacaoException;
import com.devsdoagi.agicripto.exception.usuarios.ExistingUserException;
import com.devsdoagi.agicripto.exception.usuarios.NotExistingUserException;
import com.devsdoagi.agicripto.exception.usuarios.PermissaoDeUsuarioException;
import com.devsdoagi.agicripto.model.Carteira;
import com.devsdoagi.agicripto.model.Usuarios;
import com.devsdoagi.agicripto.repository.CarteiraRepository;
import com.devsdoagi.agicripto.repository.UsuariosRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UsuariosService {

    private final UsuariosRepository usuarioRepository;
    private final CarteiraRepository carteiraRepository;
    private final PasswordEncoder passwordEncoder;

    // ===================== CONSTRUTOR DA CLASSE (INJEÇÃO DE DEPENDÊNCIA) =====================
    public UsuariosService(UsuariosRepository usuarioRepository, CarteiraRepository carteiraRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.carteiraRepository = carteiraRepository;
        this.passwordEncoder = passwordEncoder;
    }
    // ===================== VALIDAR SESSÃO ATIVA E OBTER ID DO USUÁRIO LOGADO =====================
    public Integer checarSessaoEObterIdUsuario(HttpSession session) {

        if (session == null || session.getAttribute("LOGADO") == null || !(Boolean) session.getAttribute("LOGADO")) {
            throw new AutenticacaoException("Acesso negado. Usuário não autenticado ou sessão expirada.");
        }
        return (Integer) session.getAttribute("USUARIO_ID");
    }
    // =====================  CADASTRAR NOVO CLIENTE E CRIAR SUA CARTEIRA INICIAL =====================
    @Transactional
    public Usuarios cadastrarCliente(CadastroUsuariosRequestDTO usuarioDTO) {

        if(usuarioRepository.existsByEmail(usuarioDTO.email())) {
            throw new ExistingUserException("Já existe um cadastro com esse e-mail");
        }

        if(usuarioRepository.existsByCpf(usuarioDTO.cpf())) {
            throw new ExistingUserException("Já existe um cadastro com esse cpf");
        }
        Usuarios usuario = Usuarios.builder()
                                   .cpf(usuarioDTO.cpf())
                                   .nome(usuarioDTO.nome())
                                   .senha(passwordEncoder.encode(usuarioDTO.senha()))
                                   .email(usuarioDTO.email())
                                   .celular(usuarioDTO.celular())
                                   .tipo("Cliente")
                                   .build();

        Usuarios novoCliente = usuarioRepository.save(usuario);

        Carteira novaCarteira = new Carteira();
        novaCarteira.setUsuarios(novoCliente);
        novaCarteira.setData_criacao(LocalDateTime.now());
        carteiraRepository.save(novaCarteira);

        return novoCliente;
    }
    // ===================== CADASTRAR NOVO USUÁRIO COMO ADMINISTRADOR =====================
    @Transactional
    public Usuarios cadastrarAdmin(CadastroUsuariosRequestDTO usuarioDTO) {

        if(usuarioRepository.existsByEmail(usuarioDTO.email())) {
            throw new ExistingUserException("Já existe um cadastro com esse e-mail");
        }

        if(usuarioRepository.existsByCpf(usuarioDTO.cpf())) {
            throw new ExistingUserException("Já existe um cadastro com esse cpf");
        }
        Usuarios usuario = Usuarios.builder()
                .cpf(usuarioDTO.cpf())
                .nome(usuarioDTO.nome())
                .senha(passwordEncoder.encode(usuarioDTO.senha()))
                .email(usuarioDTO.email())
                .celular(usuarioDTO.celular())
                .tipo("Admin")
                .build();

        Usuarios novoAdmin = usuarioRepository.save(usuario);

        return novoAdmin;
    }
    // ===================== AUTENTICAR USUÁRIO VERIFICANDO E-MAIL E SENHA (SEM ENCODER) =====================
    public Usuarios autenticar(LoginRequestDTO loginRequest) {

        Usuarios usuario = usuarioRepository.findByEmail(loginRequest.email()).orElseThrow(() -> new AutenticacaoException("E-mail ou senha inválidos."));

        if (!loginRequest.senha().equals(usuario.getSenha())) {
            throw new AutenticacaoException("E-mail ou senha inválidos.");
        }
        return usuario;
    }
    // ===================== BUSCAR USUÁRIO NO REPOSITÓRIO PELO ID =====================
    public Usuarios buscarUsuarioPorId(Integer userId) {

        return usuarioRepository.findById(userId).orElseThrow(() -> new AutenticacaoException("Usuário não cadastrado com o ID: " + userId));

    }
    // ===================== VALIDAR SE O USUÁRIO POSSUI PERMISSÃO DE ADMINISTRADOR =====================
    public void validarUsuarioAdmin(Integer userId) {

        Usuarios usuario = usuarioRepository.findById(userId).orElseThrow(() -> new AutenticacaoException("Usuário não cadastrado."));

        if(!usuario.getTipo().equals("Admin")) {
            throw new PermissaoDeUsuarioException("Usuário não é um administrador");
        }

    }
    // ===================== LISTAR TODOS OS USUÁRIOS COM O TIPO "CLIENTE" =====================
    public List<Usuarios> listarClientes() {
        return usuarioRepository.findByTipo("Cliente");
    }
    // ===================== LISTAR TODOS OS USUÁRIOS COM O TIPO "ADMIN" =====================
    public List<Usuarios> listarAdmins() {
        return usuarioRepository.findByTipo("Admin");
    }
    // ===================== DELETAR PERFIL DO CLIENTE PELO PRÓPRIO ID =====================
    @Transactional
    public void autoDeletarPerfilCliente(Integer userId) {

        if(usuarioRepository.existsById(userId)) {
            usuarioRepository.deleteById(userId);
        }
        else {
            throw new NotExistingUserException("Cliente não existente no banco de dados.");
        }
    }
}
