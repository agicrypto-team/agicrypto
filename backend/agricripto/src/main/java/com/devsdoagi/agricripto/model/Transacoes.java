package com.devsdoagi.agricripto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transacoes")
public class Transacoes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String tipo;

    @JoinColumn(name = "id_cliente", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuarios usuarios;

    @JoinColumn(name = "id_criptomoeda", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Criptomoedas criptomoeda;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(precision = 36, scale = 18, nullable = false)
    private BigDecimal quantidade_cripto = BigDecimal.ZERO;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime momento;
}
