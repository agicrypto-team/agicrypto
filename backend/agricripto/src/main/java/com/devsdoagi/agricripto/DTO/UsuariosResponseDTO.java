package com.devsdoagi.agricripto.DTO;

import com.devsdoagi.agricripto.model.Usuarios;

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
