package com.devsdoagi.agicripto.DTO;

public record CriptomoedasRequestDTO(
        String nome,
        String sigla,
        String icone,
        Integer id_responsavel
) {
}
