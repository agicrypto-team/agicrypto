package com.devsdoagi.agricripto.service;

import com.devsdoagi.agricripto.exception.*;
import com.devsdoagi.agricripto.model.Carteira;
import com.devsdoagi.agricripto.model.Usuarios;
import com.devsdoagi.agricripto.repository.UsuariosRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UsuariosService {

    private UsuariosRepository usuarioRepository;


    public UsuariosService(UsuariosRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuarios cadastrar(Usuarios usuario) {

        if(usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new ExistingUserException("Já existe um cadastro com esse e-mail");
        }

        if(usuarioRepository.existsByCpf(usuario.getCpf())) {
            throw new ExistingUserException("Já existe um cadastro com esse cpf");
        }


    }

    public List<Usuarios> listarClientes() {

        return usuarioRepository.findByTipo("Cliente");

    }

    public List<Usuarios> listarAdmins() {

        return usuarioRepository.findByTipo("Admin");

    }

}
