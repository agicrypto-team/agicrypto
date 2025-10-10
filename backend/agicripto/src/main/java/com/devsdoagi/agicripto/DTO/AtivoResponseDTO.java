package com.devsdoagi.agicripto.DTO;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record AtivoResponseDTO (

        String nome,
        String sigla,
        String icone,
        BigDecimal valorComprado,
        BigDecimal quantidade,
        BigDecimal cotacaoAtual,
        BigDecimal valorAtualMercado,
        BigDecimal rendimento,
        BigDecimal rendimentoPercentual

) {}
