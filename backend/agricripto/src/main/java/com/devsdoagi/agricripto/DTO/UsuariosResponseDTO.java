package com.devsdoagi.agricripto.DTO;

public record UsuariosResponseDTO(

    String cpf;
    String nome;
    String email;
    String celular;

) {

    public UsuariosResponseDTO(Usuarios usuario) {

        this(usuario.getCpf(),
             usuario.getNome(),
             usuario.getEmail(),
             usuario.getCelular());

    }

}
