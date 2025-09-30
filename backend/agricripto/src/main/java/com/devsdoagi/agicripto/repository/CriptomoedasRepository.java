package com.devsdoagi.agicripto.repository;

import com.devsdoagi.agicripto.model.Criptomoedas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// O JpaRepository fornece métodos como save(), findById(), findAll(), etc.
@Repository

public interface CriptomoedasRepository extends JpaRepository<Criptomoedas, Integer> {
    // Metodo que o Spring Data JPA implementa automaticamente com base no nome.
    // Ele buscará uma criptomoeda pela sua sigla.
    Optional<Criptomoedas> findBySigla(String sigla);
}
