package com.devsdoagi.agricripto.repository;

import com.devsdoagi.agricripto.model.Transacoes;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransacoesRepository extends JpaRepository<Transacoes, Integer> {
    List<Transacoes> findByUsuariosId(Integer idUsuario);
}
