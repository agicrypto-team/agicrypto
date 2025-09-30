package com.devsdoagi.agicripto.repository;

import com.devsdoagi.agicripto.model.Usuarios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuarios, Integer> {

    Optional<Usuarios> findByEmail(String email);

    Optional<Usuarios> findByCpf(String cpf);

    List<Usuarios> findByTipo(String tipo);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

}
