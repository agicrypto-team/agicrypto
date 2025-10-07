package com.devsdoagi.agicripto.DTO;
import com.devsdoagi.agicripto.model.AtivosCarteira;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AtivosCarteiraResponseDTO {
    private Integer id;

    // Dados Essenciais da Criptomoeda (para evitar expor a Entity completa)
    private Integer idCriptomoeda;
    private String siglaCriptomoeda;
    private String nomeCriptomoeda;

    // Dados Essenciais da Carteira
    private Integer idCarteira;

    private BigDecimal quantidade;
    private BigDecimal precoMedio;
    private LocalDateTime dataAtualizacao;

    // Construtor que mapeia a Entity para o DTO
    public AtivosCarteiraResponseDTO(AtivosCarteira entity) {
        this.id = entity.getId();
        this.quantidade = entity.getQuantidade();
        this.precoMedio = entity.getPrecoMedio();
        this.dataAtualizacao = entity.getDataAtualizacao();

        if (entity.getCriptomoedas() != null) {
            this.idCriptomoeda = entity.getCriptomoedas().getId();
            this.siglaCriptomoeda = entity.getCriptomoedas().getSigla();
            this.nomeCriptomoeda = entity.getCriptomoedas().getNome();
        }

        if (entity.getCarteira() != null) {
            this.idCarteira = entity.getCarteira().getId();
        }
    }

}