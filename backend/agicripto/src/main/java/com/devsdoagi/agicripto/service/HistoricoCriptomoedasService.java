package com.devsdoagi.agicripto.service;


import com.devsdoagi.agicripto.exception.historicoCriptomoedas.HistoricoCriptomoedaNaoEncontradoException;
import com.devsdoagi.agicripto.model.HistoricoCriptomoedas;
import com.devsdoagi.agicripto.repository.CriptomoedasRepository;
import com.devsdoagi.agicripto.repository.HistoricoCriptomoedasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class HistoricoCriptomoedasService {

    private final CriptomoedasRepository criptomoedasRepository;
    private final HistoricoCriptomoedasRepository historicoCriptomoedasRepository;
    private final CriptomoedasService criptomoedasService;

    public HistoricoCriptomoedas buscarPorId(Integer id) {
        return historicoCriptomoedasRepository.findById(id)
                .orElseThrow(() -> new HistoricoCriptomoedaNaoEncontradoException(id));
    }


}
