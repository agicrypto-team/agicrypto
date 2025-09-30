package com.devsdoagi.agicripto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "historico_criptomoedas")
public class HistoricoCriptomoedas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "id_criptomoeda")
    @ManyToOne(fetch = FetchType.LAZY)
    private Criptomoedas criptomoedas;

    @Column(nullable = false, precision = 18, scale = 8)
    private BigDecimal cotacao_momento;

    @Column(nullable = false)
    private LocalDateTime momento;
}
