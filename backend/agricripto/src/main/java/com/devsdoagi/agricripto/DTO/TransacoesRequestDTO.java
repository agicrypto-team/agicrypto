package com.devsdoagi.agricripto.DTO;

import java.math.BigDecimal;

public class TransacoesRequestDTO {

    private String tipo;
    private BigDecimal valor;
    private Integer usuarioId;
    private Integer criptomoedaId;

    // Getters e Setters
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getCriptomoedaId() {
        return criptomoedaId;
    }

    public void setCriptomoedaId(Integer criptomoedaId) {
        this.criptomoedaId = criptomoedaId;
    }
}
