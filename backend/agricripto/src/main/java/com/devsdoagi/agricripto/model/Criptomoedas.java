package com.devsdoagi.agricripto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

// Ignora o campo 'usuarios' durante a serialização JSON
// e também o objeto proxy do Hibernate.
@JsonIgnoreProperties({"usuarios", "hibernateLazyInitializer"})
@Table(name = "criptomoedas")
public class Criptomoedas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Mapeia a chave estrangeira corretamente, garantindo que
    // o campo não possa ser nulo para corresponder ao banco de dados.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_responsavel", nullable = false)
    private Usuarios usuarios;

    @Column(name = "nome", length = 60, nullable = false)
    private String nome;

    @Column(name = "sigla", length = 10, nullable = false)
    private String sigla;

    @Column(name = "icone", length = 255)
    private String icone;

    @Column(name = "momento_cadastro")
    private LocalDateTime momentoCadastro;
}
