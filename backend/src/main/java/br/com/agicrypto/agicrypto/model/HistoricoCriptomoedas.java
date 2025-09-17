package br.com.agicrypto.agicrypto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

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

    @Column(nullable = false)
    private Double cotacao_momento;

    @Column(nullable = false)
    private LocalDateTime momento;
}
