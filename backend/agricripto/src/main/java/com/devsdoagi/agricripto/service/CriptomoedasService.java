package com.devsdoagi.agricripto.service;
import com.devsdoagi.agricripto.model.Criptomoedas;
import com.devsdoagi.agricripto.repository.CriptomoedasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CriptomoedasService {
    @Autowired
    private CriptomoedasRepository criptomoedasRepository;

    // Busca todas as criptomoedas.
    public List<Criptomoedas> findAll() {
        return criptomoedasRepository.findAll();
    }

    // Busca uma criptomoeda pelo ID.
    public Optional<Criptomoedas> findById(Integer id) {
        return criptomoedasRepository.findById(id);
    }
}
