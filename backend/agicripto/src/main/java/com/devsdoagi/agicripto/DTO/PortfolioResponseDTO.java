package com.devsdoagi.agicripto.DTO;

import java.math.BigDecimal;
import java.util.List;

public record PortfolioResponseDTO (

        BigDecimal valorTotalComprado,
        BigDecimal patrimonioTotal,
        BigDecimal rendimentoTotal,
        BigDecimal rendimentoPercentualTotal,
        List<AtivoResponseDTO> listaAtivos

) {}
