package com.devsdoagi.agicripto.service;

import com.devsdoagi.agicripto.model.Usuarios;
import com.devsdoagi.agicripto.repository.CarteiraRepository;
import com.devsdoagi.agicripto.repository.UsuariosRepository;
import com.devsdoagi.agicripto.model.Carteira;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CarteiraService {

    @Autowired
    private CarteiraRepository carteiraRepository;

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Transactional
    public Carteira CriarCarteira(Usuarios usuarios) {
        Usuarios checkUsuario = usuariosRepository.findById(usuarios.getId())
                .orElseThrow(() -> new RuntimeException("Erro, id Inexistente"));
        Carteira carteiraUser = new Carteira();
        carteiraUser.setUsuarios(checkUsuario);
        carteiraUser.setPatrimonio_total(BigDecimal.ZERO);
        carteiraUser.setMomento_atualizacao(LocalDateTime.now());

        return carteiraRepository.save(carteiraUser);
    }
}
