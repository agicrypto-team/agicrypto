package com.devsdoagi.agricripto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "criptomoedas")
public class Criptomoedas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome", length = 60, nullable = false)
    private String nome;

    @Column(name = "sigla", length = 10, nullable = false)
    private String sigla;

    @Column(name = "icone", length = 254)
    private String icone;

    @JoinColumn(name = "id_responsavel", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuarios usuarios;

    @Column(name = "momento_cadastro")
    private LocalDateTime momento_cadastro;
}
