package com.devsdoagi.agicripto.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CarteiraResponseDTO {
    private Integer id;
    private Integer idCliente;
    // private BigDecimal patrimonioTotal;
    private LocalDateTime dataCriacao;

    public CarteiraResponseDTO(
            Integer id,
            Integer idCliente,
            BigDecimal patrimonioTotal,
            LocalDateTime dataCriacao) {
        this.id = id;
        this.idCliente = idCliente;
        // this.patrimonioTotal = patrimonioTotal;
        this.dataCriacao = dataCriacao;
    }
}
