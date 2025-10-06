package com.devsdoagi.agicripto.DTO.usuarios;

import com.devsdoagi.agicripto.model.Usuarios;

public record UsuariosResponseDTO(

    String cpf,
    String nome,
    String email,
    String celular

) {

    public UsuariosResponseDTO(Usuarios usuario) {

        this(usuario.getCpf(),
             usuario.getNome(),
             usuario.getEmail(),
             usuario.getCelular());

    }

}
