package com.devsdoagi.agicripto.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.devsdoagi.agicripto.model.Transacoes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransacoesResponseDTO {

    private Integer id;
    private String tipo;
    private BigDecimal valor;
    private Double quantidadeCripto;
    private LocalDateTime momento;
    private Integer usuarioId;
    private String usuarioNome;
    private Integer criptomoedaId;
    private String criptomoedaNome;

    // Construtor que converte automaticamente a entidade Transacoes em DTO
    public TransacoesResponseDTO(Transacoes t) {
        this.id = t.getId();
        this.tipo = t.getTipo();
        this.valor = t.getValor();
        this.quantidadeCripto = t.getQuantidade_cripto().doubleValue();
        this.momento = t.getMomento();

        if (t.getUsuarios() != null) {
            this.usuarioId = t.getUsuarios().getId();
            this.usuarioNome = t.getUsuarios().getNome();
        }

        if (t.getCriptomoeda() != null) {
            this.criptomoedaId = t.getCriptomoeda().getId();
            this.criptomoedaNome = t.getCriptomoeda().getNome();
        }
    }
}

