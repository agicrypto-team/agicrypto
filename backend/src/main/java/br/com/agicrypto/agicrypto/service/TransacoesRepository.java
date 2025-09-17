package br.com.agicrypto.agicrypto.service;

import br.com.agicrypto.agicrypto.model.Transacoes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacoesRepository extends JpaRepository<Transacoes, Integer> {
}
