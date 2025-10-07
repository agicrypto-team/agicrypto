package com.devsdoagi.agicripto.DTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AtivosCarteiraRequestDTO {
    private Integer idCarteira; // Corresponde ao id_carteira
    private Integer idCriptomoeda; // Corresponde ao id_criptomoeda

    private BigDecimal quantidade;
    private BigDecimal precoMedio;
}
