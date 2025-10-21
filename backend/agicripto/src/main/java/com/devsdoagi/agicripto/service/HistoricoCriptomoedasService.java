package com.devsdoagi.agicripto.service;


import com.devsdoagi.agicripto.DTO.HistoricoCriptomoedasResponseDTO;
import com.devsdoagi.agicripto.exception.historicoCriptomoedas.HistoricoCriptomoedaNaoEncontradoException;
import com.devsdoagi.agicripto.model.Criptomoedas;
import com.devsdoagi.agicripto.model.HistoricoCriptomoedas;
import com.devsdoagi.agicripto.repository.CriptomoedasRepository;
import com.devsdoagi.agicripto.repository.HistoricoCriptomoedasRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class HistoricoCriptomoedasService {

    @Autowired
    private final HistoricoCriptomoedasRepository historicoCriptomoedasRepository;
    private final CriptomoedasRepository criptomoedasRepository;
    private final CriptomoedasService criptomoedasService;

    // ===================== BUSCAR HISTÓRICO DE COTAÇÃO POR ID =====================
    public HistoricoCriptomoedas buscarPorId(Integer id) {
        return historicoCriptomoedasRepository.findById(id)
                .orElseThrow(() -> new HistoricoCriptomoedaNaoEncontradoException(id));
    }

    // ===================== LISTAR TODOO HISTÓRICO DE COTAÇÕES E CONVERTER PARA DTO =====================
    public List<HistoricoCriptomoedasResponseDTO> listarTodosComCriptomoeda() {
        List<HistoricoCriptomoedas> historicos = historicoCriptomoedasRepository.findAll();

        return historicos.stream()
                .map(HistoricoCriptomoedasResponseDTO::new)
                .toList();
    }
    // ===================== LISTAR HISTÓRICO DE COTAÇÕES POR ID DA CRIPTOMOEDA E CONVERTER PARA DTO =====================
    public List<HistoricoCriptomoedasResponseDTO> listarPorCriptomoeda(Integer idCriptomoeda) {
        return historicoCriptomoedasRepository.findByCriptomoedas_Id(idCriptomoeda)
                .stream()
                .map(HistoricoCriptomoedasResponseDTO::new)
                .toList();
    }
    // ===================== ATUALIZAR COTAÇÕES DE TODAS AS CRIPTOMOEDAS PERIODICAMENTE =====================
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void atualizarCotaçõesPeriodicamente() {
        List<Criptomoedas> criptomoedas = criptomoedasRepository.findAll();

        for (Criptomoedas cripto : criptomoedas) {
            try {
                BigDecimal cotacaoBrl = criptomoedasService.buscarCotacaoEmBRL(cripto.getNome());

                if (cotacaoBrl.compareTo(BigDecimal.ZERO) > 0) {
                    HistoricoCriptomoedas historico = new HistoricoCriptomoedas();
                    historico.setCriptomoedas(cripto);
                    historico.setCotacao_momento(cotacaoBrl);
                    historico.setMomento(LocalDateTime.now());

                    historicoCriptomoedasRepository.save(historico);

                    System.out.println("Histórico salvo: " + cripto.getNome() + " | R$ " + cotacaoBrl);
                } else {
                    System.err.println("Cotação inválida para " + cripto.getNome());
                }

            } catch (Exception e) {
                System.err.println("Erro ao atualizar " + cripto.getNome() + ": " + e.getMessage());
            }
        }
    }
    // ===================== FUNÇÃO: OBTER A COTAÇÃO MAIS RECENTE DA CRIPTOMOEDA =====================
    public BigDecimal obterCotacaoAtual(Integer idCriptomoeda) {
        return historicoCriptomoedasRepository
                .findTopByCriptomoedas_IdOrderByMomentoDesc(idCriptomoeda)
                .map(HistoricoCriptomoedas::getCotacao_momento)
                .orElse(null);
    }
}