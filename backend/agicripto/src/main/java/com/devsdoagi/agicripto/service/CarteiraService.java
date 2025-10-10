package com.devsdoagi.agicripto.service;

import com.devsdoagi.agicripto.DTO.AtivoResponseDTO;
import com.devsdoagi.agicripto.DTO.PortfolioResponseDTO;
import com.devsdoagi.agicripto.model.AtivosCarteira;
import com.devsdoagi.agicripto.repository.AtivosCarteiraRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CarteiraService {

    private final HistoricoCriptomoedasService historicoCriptomoedasService;
    private final AtivosCarteiraRepository ativosCarteiraRepository;

    public CarteiraService(AtivosCarteiraRepository ativosCarteiraRepository, HistoricoCriptomoedasService historicoCriptomoedasService) {

        this.ativosCarteiraRepository = ativosCarteiraRepository;
        this.historicoCriptomoedasService = historicoCriptomoedasService;
    }

    public PortfolioResponseDTO obterPortfolioCliente(Integer userId) {

        List<AtivosCarteira> listaAtivos = ativosCarteiraRepository.findByCarteira_Usuarios_Id(userId);

        List<AtivoResponseDTO> listaAtivosDTO = listaAtivos.stream().map(ativo -> {

            BigDecimal cotacaoAtual = historicoCriptomoedasService.obterCotacaoAtual(ativo.getCriptomoedas().getId());
            BigDecimal valorAtualMercado = ativo.getQuantidade().multiply(cotacaoAtual);
            BigDecimal rendimento = valorAtualMercado.subtract(ativo.getValorTotalComprado());
            int precisao = 4;
            BigDecimal rendimentoPercentual;
            if (ativo.getValorTotalComprado().compareTo(BigDecimal.ZERO) <= 0) {

                rendimentoPercentual = BigDecimal.ZERO;

            } else {

                rendimentoPercentual = rendimento
                        .divide(ativo.getValorTotalComprado(), precisao, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100.00"));

            }

            return new AtivoResponseDTO(
                    ativo.getCriptomoedas().getNome(),
                    ativo.getCriptomoedas().getSigla(),
                    ativo.getCriptomoedas().getIcone(),
                    ativo.getValorTotalComprado(),
                    ativo.getQuantidade(),
                    cotacaoAtual,
                    valorAtualMercado,
                    rendimento,
                    rendimentoPercentual
                    );

        }).toList();

        BigDecimal patrimonioTotal = listaAtivosDTO.stream().map(AtivoResponseDTO::valorAtualMercado).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorTotalComprado = listaAtivosDTO.stream().map(AtivoResponseDTO::valorComprado).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal rendimentoTotal = listaAtivosDTO.stream().map(AtivoResponseDTO::rendimento).reduce(BigDecimal.ZERO, BigDecimal::add);

        int precisao = 4;
        BigDecimal rendimentoPercentualTotal;
        if (valorTotalComprado.compareTo(BigDecimal.ZERO) <= 0) {

            rendimentoPercentualTotal = BigDecimal.ZERO;

        } else {

            rendimentoPercentualTotal = rendimentoTotal
                    .divide(valorTotalComprado, precisao, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100.00"));

        }

        return new PortfolioResponseDTO(valorTotalComprado, patrimonioTotal, rendimentoTotal, rendimentoPercentualTotal, listaAtivosDTO);

    }

}
