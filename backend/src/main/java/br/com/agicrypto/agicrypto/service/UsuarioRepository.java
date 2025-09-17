package br.com.agicrypto.agicrypto.service;

import br.com.agicrypto.agicrypto.model.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuarios, Integer> {
}
