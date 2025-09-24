package com.devsdoagi.agricripto.model.Transacoes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransacoesDTO {

    private Integer id;
    private String tipo;
    private BigDecimal valor;
    private LocalDateTime momento;

    private Integer usuarioId;
    private String usuarioNome;

    private Integer criptomoedaId;
    private String criptomoedaNome;

    // Construtores
    public TransacoesDTO() {}

    public TransacoesDTO(Integer id, String tipo, BigDecimal valor, LocalDateTime momento,
                         Integer usuarioId, String usuarioNome,
                         Integer criptomoedaId, String criptomoedaNome) {
        this.id = id;
        this.tipo = tipo;
        this.valor = valor;
        this.momento = momento;
        this.usuarioId = usuarioId;
        this.usuarioNome = usuarioNome;
        this.criptomoedaId = criptomoedaId;
        this.criptomoedaNome = criptomoedaNome;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDateTime getMomento() { return momento; }
    public void setMomento(LocalDateTime momento) { this.momento = momento; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public String getUsuarioNome() { return usuarioNome; }
    public void setUsuarioNome(String usuarioNome) { this.usuarioNome = usuarioNome; }

    public Integer getCriptomoedaId() { return criptomoedaId; }
    public void setCriptomoedaId(Integer criptomoedaId) { this.criptomoedaId = criptomoedaId; }

    public String getCriptomoedaNome() { return criptomoedaNome; }
    public void setCriptomoedaNome(String criptomoedaNome) { this.criptomoedaNome = criptomoedaNome; }
}

