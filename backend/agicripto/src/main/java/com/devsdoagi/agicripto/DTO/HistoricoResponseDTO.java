package com.devsdoagi.agicripto.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record HistoricoResponseDTO (

        String tipoTransacao,
        String nomeCripto,
        String siglaCripto,
        BigDecimal valorComprado,
        BigDecimal quantidadeComprada,
        LocalDateTime momentoTransacao

) {}
