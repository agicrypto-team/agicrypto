package com.devsdoagi.agricripto.model;

import com.devsdoagi.agicripto.model.Criptomoedas;
import com.devsdoagi.agicripto.model.Transacoes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuarios")
public class Usuarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // id auto increment
    private Integer id;

    @Column(nullable = false, unique = true, length = 11, columnDefinition = "CHAR(11)")
    private String cpf;

    @Column(nullable = false, length = 60)
    private String nome;

    @Column(nullable = false, length = 255) //Perguntar por que estava sem o parâmetro lenght no script antigo
    private String senha;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(nullable = false, unique = true, length = 20)//Adicionei not null e unique
    private String celular;

    @Column(nullable = false)
    private String tipo; // "Cliente" ou "Admin"

    @OneToMany(mappedBy = "id_responsavel")
    private List<Criptomoedas> criptomoedas;

    @OneToMany(mappedBy = "id_cliente")
    private List<Transacoes> transacoes;

}