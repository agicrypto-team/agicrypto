package com.devsdoagi.agicripto.service;

import com.devsdoagi.agicripto.exception.*;
import com.devsdoagi.agicripto.model.Carteira;
import com.devsdoagi.agicripto.model.Usuarios;
import com.devsdoagi.agicripto.repository.CarteiraRepository;
import com.devsdoagi.agicripto.repository.UsuariosRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UsuariosService {

    private UsuariosRepository usuarioRepository;
    private CarteiraRepository carteiraRepository;


    public UsuariosService(UsuariosRepository usuarioRepository, CarteiraRepository carteiraRepository) {
        this.usuarioRepository = usuarioRepository;
        this.carteiraRepository = carteiraRepository;
    }

    @Transactional
    public Usuarios cadastrar(Usuarios usuario) {

        if(usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new ExistingUserException("Já existe um cadastro com esse e-mail");
        }

        if(usuarioRepository.existsByCpf(usuario.getCpf())) {
            throw new ExistingUserException("Já existe um cadastro com esse cpf");
        }

        Usuarios novoUsuario = usuarioRepository.save(usuario);

        if("Cliente".equalsIgnoreCase(novoUsuario.getTipo())){
            Carteira novaCarteira = new Carteira();

            novaCarteira.setUsuarios(novoUsuario);
            novaCarteira.setMomento_atualizacao(LocalDateTime.now());
            carteiraRepository.save(novaCarteira);
        }

        return novoUsuario;
    }

    public List<Usuarios> listarClientes() {

        return usuarioRepository.findByTipo("Cliente");

    }

    public List<Usuarios> listarAdmins() {

        return usuarioRepository.findByTipo("Admin");

    }

}
