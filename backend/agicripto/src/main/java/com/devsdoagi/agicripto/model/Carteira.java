package com.devsdoagi.agicripto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

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

    @Column(nullable = false)
    private LocalDateTime data_criacao;

    @OneToMany(mappedBy = "carteira", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AtivosCarteira> ativos;

}
