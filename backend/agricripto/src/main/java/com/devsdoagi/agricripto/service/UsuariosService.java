package com.devsdoagi.agricripto.service;

import com.devsdoagi.agricripto.repository.UsuariosRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

    @Autowired
    private UsuariosRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuarios cadastrar(Usuarios usuario) {

        if(usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new ExistingUserException("Já existe um cadastro com esse e-mail");
        }

        if(usuarioRepository.existsByCpf(usuario.getCpf())) {
            throw new ExistingUserException("Já existe um cadastro com esse cpf");
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        return usuarioRepository.save(usuario);

    }

    public List<Usuarios> listarClientes() {

        return usuarioRepository.findByTipo("Cliente");

    }

    public List<Usuarios> listarAdmins() {

        return usuarioRepository.findByTipo("Admin");

    }

}
