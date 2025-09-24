package com.devsdoagi.agricripto.model;

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
@Table(name = "carteiras")
public class Carteira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "id_cliente", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private Usuarios usuarios;

    @Column(precision = 18, scale = 8, nullable = false)
    private BigDecimal patrimonio_total = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime momento_atualizacao;

}
