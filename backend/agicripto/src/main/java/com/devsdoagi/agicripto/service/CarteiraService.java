package com.devsdoagi.agicripto.service;

import com.devsdoagi.agicripto.DTO.AtivoResponseDTO;
import com.devsdoagi.agicripto.DTO.AtivoVenderResponseDTO;
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

    private static final int CURRENCY_SCALE = 2;
    private static final int PERCENTAGE_SCALE = 4;
        private static final int QUANTIDADE_SCALE = 8;


    public CarteiraService(AtivosCarteiraRepository ativosCarteiraRepository, HistoricoCriptomoedasService historicoCriptomoedasService) {
        this.ativosCarteiraRepository = ativosCarteiraRepository;
        this.historicoCriptomoedasService = historicoCriptomoedasService;
    }

    public PortfolioResponseDTO obterPortfolioCliente(Integer userId) {
        List<AtivosCarteira> listaAtivos = ativosCarteiraRepository.findByCarteira_Usuarios_Id(userId);

        List<AtivoResponseDTO> listaAtivosDTO = listaAtivos.stream().map(ativo -> {
            BigDecimal cotacaoAtual = historicoCriptomoedasService.obterCotacaoAtual(ativo.getCriptomoedas().getId());
            BigDecimal valorAtualMercado = ativo.getQuantidade().multiply(cotacaoAtual).setScale(CURRENCY_SCALE, RoundingMode.HALF_UP);
            BigDecimal rendimento = valorAtualMercado.subtract(ativo.getValorTotalComprado()).setScale(CURRENCY_SCALE, RoundingMode.HALF_UP);
            BigDecimal rendimentoPercentual;

            if (ativo.getValorTotalComprado().compareTo(BigDecimal.ZERO) <= 0) {
                rendimentoPercentual = BigDecimal.ZERO.setScale(CURRENCY_SCALE, RoundingMode.HALF_UP);
            } else {
                rendimentoPercentual = rendimento
                        .divide(ativo.getValorTotalComprado(), PERCENTAGE_SCALE, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100.00")).setScale(CURRENCY_SCALE, RoundingMode.HALF_UP);
            }

            return new AtivoResponseDTO(
                    ativo.getCriptomoedas().getNome(),
                    ativo.getCriptomoedas().getSigla(),
                    ativo.getCriptomoedas().getIcone(),
                    ativo.getValorTotalComprado().setScale(CURRENCY_SCALE, RoundingMode.HALF_UP),
                    ativo.getQuantidade().setScale(QUANTIDADE_SCALE, RoundingMode.HALF_UP),
                    cotacaoAtual.setScale(CURRENCY_SCALE, RoundingMode.HALF_UP),
                    valorAtualMercado,
                    rendimento,
                    rendimentoPercentual
            );
        }).toList();

        BigDecimal patrimonioTotal = listaAtivosDTO.stream()
                .map(AtivoResponseDTO::valorAtualMercado)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(CURRENCY_SCALE, RoundingMode.HALF_UP);

        BigDecimal valorTotalComprado = listaAtivosDTO.stream()
                .map(AtivoResponseDTO::valorComprado)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(CURRENCY_SCALE, RoundingMode.HALF_UP);

        BigDecimal rendimentoTotal = listaAtivosDTO.stream()
                .map(AtivoResponseDTO::rendimento)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(CURRENCY_SCALE, RoundingMode.HALF_UP);

        BigDecimal rendimentoPercentualTotal;

        if (valorTotalComprado.compareTo(BigDecimal.ZERO) <= 0) {
            rendimentoPercentualTotal = BigDecimal.ZERO.setScale(CURRENCY_SCALE, RoundingMode.HALF_UP);
        } else {
            rendimentoPercentualTotal = rendimentoTotal
                    .divide(valorTotalComprado, PERCENTAGE_SCALE, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100.00")).setScale(CURRENCY_SCALE, RoundingMode.HALF_UP);
        }

        return new PortfolioResponseDTO(
                valorTotalComprado,
                patrimonioTotal,
                rendimentoTotal,
                rendimentoPercentualTotal,
                listaAtivosDTO
        );
    }

    // ✅ NOVO METODO: lista apenas as criptomoedas que o usuário possui
    public List<AtivoVenderResponseDTO> listarCriptomoedasUsuario(Integer userId) {
        return ativosCarteiraRepository.findByCarteira_Usuarios_Id(userId)
                .stream()
                .map(ativo -> new AtivoVenderResponseDTO(
                        ativo.getCriptomoedas().getNome(),
                        ativo.getCriptomoedas().getSigla()
                ))
                .toList();
    }
}
