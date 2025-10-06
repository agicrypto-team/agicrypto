package com.devsdoagi.agicripto.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CarteiraResponseDTO {
    private Integer id;
    private Integer idCliente;
    private BigDecimal patrimonioTotal;
    private LocalDateTime momentoAtualizacao;

    public CarteiraResponseDTO(
            Integer id,
            Integer idCliente,
            BigDecimal patrimonioTotal,
            LocalDateTime momentoAtualizacao) {
        this.id = id;
        this.idCliente = idCliente;
        this.patrimonioTotal = patrimonioTotal;
        this.momentoAtualizacao = momentoAtualizacao;
    }
}
