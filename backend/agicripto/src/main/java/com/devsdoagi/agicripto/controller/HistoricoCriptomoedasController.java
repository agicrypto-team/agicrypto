package com.devsdoagi.agicripto.controller;

import com.devsdoagi.agicripto.DTO.HistoricoCriptomoedasResponseDTO;
import com.devsdoagi.agicripto.model.HistoricoCriptomoedas;
import com.devsdoagi.agicripto.repository.HistoricoCriptomoedasRepository;
import com.devsdoagi.agicripto.service.HistoricoCriptomoedasService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/historicos")
@RequiredArgsConstructor
public class HistoricoCriptomoedasController {

    @Autowired
    private final HistoricoCriptomoedasService historicoCriptomoedasService;

    @GetMapping
    public ResponseEntity<List<HistoricoCriptomoedasResponseDTO>> listarTodos() {
        return ResponseEntity.ok(historicoCriptomoedasService.listarTodosComCriptomoeda());
    }

    @GetMapping("/{idCriptomoeda}")
    public ResponseEntity<List<HistoricoCriptomoedasResponseDTO>> listarPorCriptomoeda(@PathVariable Integer idCriptomoeda) {
        return ResponseEntity.ok(historicoCriptomoedasService.listarPorCriptomoeda(idCriptomoeda));
    }

    @GetMapping("/{idCriptomoeda}/cotacao-atual")
    public ResponseEntity<BigDecimal> obterCotacaoAtual(@PathVariable Integer idCriptomoeda) {
        BigDecimal cotacao = historicoCriptomoedasService.obterCotacaoAtual(idCriptomoeda);
        if (cotacao == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cotacao);
    }


    /*
    @GetMapping("/{id}")
    public HistoricoCriptomoedas encontrarPorId(@PathVariable Integer id) {
        return historicoCriptomoedasService.buscarPorId(id);
    }

    @GetMapping("/grafico/{idCriptomoeda}")
    public List<HistoricoCriptomoedas> listarPorCriptomoeda(@PathVariable Integer idCriptomoeda) {
        return historicoCriptomoedasService.listarPorCriptomoeda(idCriptomoeda);
    }

    */


}