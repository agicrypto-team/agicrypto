package com.devsdoagi.agicripto.controller;

import com.devsdoagi.agicripto.model.HistoricoCriptomoedas;
import com.devsdoagi.agicripto.repository.HistoricoCriptomoedasRepository;
import com.devsdoagi.agicripto.service.HistoricoCriptomoedasService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/historico")
@RequiredArgsConstructor
public class HistoricoCriptomoedasController {

    private final HistoricoCriptomoedasRepository historicoCriptomoedasRepository;
    private final HistoricoCriptomoedasService historicoCriptomoedasService;

    @GetMapping("/{id}")
    public HistoricoCriptomoedas encontrarPorId(@PathVariable Integer id) {
        return historicoCriptomoedasService.buscarPorId(id);
    }

}
