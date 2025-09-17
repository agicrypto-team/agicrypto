package br.com.agicrypto.agicrypto.service;

import br.com.agicrypto.agicrypto.model.Criptomoedas;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CriptomoedasRepository extends JpaRepository<Criptomoedas, Integer> {
}
