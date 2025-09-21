package com.devsdoagi.agricripto.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuarios")
public class Usuarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // id auto increment
    private Integer id;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false, length = 60)
    private String nome;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(length = 20)
    private String celular;

    @Column(nullable = false)
    private String tipo; // "Cliente" ou "Admin"
}