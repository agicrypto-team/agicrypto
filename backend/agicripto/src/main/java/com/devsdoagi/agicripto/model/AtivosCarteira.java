package com.devsdoagi.agicripto.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ativos_carteira")
@Data
@NoArgsConstructor
public class AtivosCarteira {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // int

    // Mapeamento 'carteiras'
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carteira", nullable = false)
    private Carteira carteira; // id_carteira int

    // Mapeamento 'criptomoedas'
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_criptomoeda", nullable = false)
    private Criptomoedas criptomoedas; // id_criptomoeda int

    @Column(name = "quantidade", precision = 20, scale = 8, nullable = false)
    private BigDecimal quantidade; // decimal(20,8)

    @Column(name = "preco_medio", precision = 20, scale = 8, nullable = false)
    private BigDecimal precoMedio; // decimal(20,8)

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao; // timestamp (usando LocalDateTime para mapeamento comum em Spring Boot)

}
