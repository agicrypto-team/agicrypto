package com.devsdoagi.agicripto.DTO;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AtivosCarteiraRequestDTO {
    private Integer idCarteira;
    private Integer idCriptomoeda;

    private BigDecimal quantidade;
    private BigDecimal valorTotalComprado;
}
