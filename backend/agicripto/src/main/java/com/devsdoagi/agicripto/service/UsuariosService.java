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

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UsuariosService {

    private UsuariosRepository usuarioRepository;
    private CarteiraRepository carteiraRepository;
    private PasswordEncoder passwordEncoder;


    public UsuariosService(UsuariosRepository usuarioRepository, CarteiraRepository carteiraRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.carteiraRepository = carteiraRepository;
        this.passwordEncoder = passwordEncoder;
    }

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
        novaCarteira.setMomento_atualizacao(LocalDateTime.now());
        carteiraRepository.save(novaCarteira);

        return novoCliente;
    }

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

        // É possível com que ao criar novo Admin não seja criada uma carteira para ele?
        Carteira novaCarteira = new Carteira();
        novaCarteira.setUsuarios(novoAdmin);
        novaCarteira.setMomento_atualizacao(LocalDateTime.now());
        carteiraRepository.save(novaCarteira);

        return novoAdmin;
    }

    public Usuarios autenticar(LoginRequestDTO loginRequest) {

        Usuarios usuario = usuarioRepository.findByEmail(loginRequest.email()).orElseThrow(() -> new AutenticacaoException("E-mail ou senha inválidos."));

        /*if (!passwordEncoder.matches(loginRequest.senha(), usuario.getSenha())) {
            throw new AutenticacaoException("E-mail ou senha inválidos.");
        }*/

        if (!loginRequest.senha().equals(usuario.getSenha())) {
            throw new AutenticacaoException("E-mail ou senha inválidos.");
        }

        return usuario;

    }

    public Usuarios buscarUsuarioPorId(Integer userId) {

        return usuarioRepository.findById(userId).orElseThrow(() -> new AutenticacaoException("Usuário não cadastrado com o ID: " + userId));

    }

    public void validarUsuarioAdmin(Integer userId) {

        Usuarios usuario = usuarioRepository.findById(userId).orElseThrow(() -> new AutenticacaoException("Usuário não cadastrado."));


        if(!usuario.getTipo().equals("Admin")) {

            throw new PermissaoDeUsuarioException("Usuário não é um administrador");

        }

    }


    public List<Usuarios> listarClientes() {

        return usuarioRepository.findByTipo("Cliente");

    }

    public List<Usuarios> listarAdmins() {

        return usuarioRepository.findByTipo("Admin");

    }

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
