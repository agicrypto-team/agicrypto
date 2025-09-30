package com.devsdoagi.agicripto.repository;

import com.devsdoagi.agicripto.model.Transacoes;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransacoesRepository extends JpaRepository<Transacoes, Integer> {
    List<Transacoes> findByUsuariosId(Integer idUsuario);
}
