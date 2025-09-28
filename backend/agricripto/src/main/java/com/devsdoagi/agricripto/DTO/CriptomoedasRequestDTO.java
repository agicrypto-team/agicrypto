package com.devsdoagi.agricripto.DTO;

 import jakarta.validation.constraints.NotBlank;
 import jakarta.validation.constraints.NotNull;

public record CriptomoedasRequestDTO(
        String nome,
        String sigla,
        String icone,
        Integer id_responsavel
) {
}
