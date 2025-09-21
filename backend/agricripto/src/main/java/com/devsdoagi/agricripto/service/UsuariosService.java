package com.devsdoagi.agricripto.service;


import com.devsdoagi.agricripto.model.Usuarios;
import com.devsdoagi.agricripto.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuariosService {

    @Autowired
    private final UsuariosRepository usuariosRepository;

    public UsuariosService (UsuariosRepository usuariosRepository) { this.usuariosRepository = usuariosRepository; }

    public List<Usuarios> listarTodos() { return usuariosRepository.findAll(); }

}
