package com.devsdoagi.agricripto.service;

import com.devsdoagi.agricripto.model.Usuarios;
import com.devsdoagi.agricripto.repository.CarteiraRepository;
import com.devsdoagi.agricripto.repository.UsuariosRepository;
import com.devsdoagi.agricripto.model.Carteira;
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
