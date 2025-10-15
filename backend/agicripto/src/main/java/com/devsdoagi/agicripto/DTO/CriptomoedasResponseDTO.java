package com.devsdoagi.agicripto.DTO;

import com.devsdoagi.agicripto.DTO.usuarios.UsuariosResponseDTO;
import com.devsdoagi.agicripto.model.Criptomoedas;

import java.math.BigDecimal;

// Record para o objeto de resposta da Criptomoeda.
// Inclui um objeto 'responsavel' que usa o UsuarioDTO para quebrar o loop.
public record CriptomoedasResponseDTO(

        Integer id,
        String nome,
        String sigla,
        String icone,
        // O DTO aninhado para evitar o loop de serialização
        UsuariosResponseDTO responsavel

) {
    // Construtor customizado para mapear da entidade JPA (Criptomoedas) para o DTO.
    public CriptomoedasResponseDTO(Criptomoedas criptomoeda) {
        this(
                criptomoeda.getId(),
                criptomoeda.getNome(),
                criptomoeda.getSigla(),
                criptomoeda.getIcone(),
                // Mapeamento do objeto Usuarios para o DTO de Resposta
                new UsuariosResponseDTO(criptomoeda.getUsuarios())
        );
    }
}
