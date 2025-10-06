package com.devsdoagi.agicripto.DTO.usuarios;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CadastroUsuariosRequestDTO (

        @NotBlank(message = "Obrigatório fornecer o cpf")
        @Size(min = 11, max = 11, message = "O cpf deve ser composto por 11 dígitos")
        @Pattern(regexp = "\\d{11}", message = "O cpf deve conter apenas dígitos numéricos")
        String cpf,

        @NotBlank(message = "Obrigatório fornecer o nome")
        @Size(max = 60, message = "O nome deve ter no máximo 60 caracteres.")
        String nome,

        @NotBlank(message = "Obrigatório fornecer a senha")
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.") //Deixamos essa verificação de tamanho mínimo de senha? Se sim, será preciso incluir isso no banco de dados em si
        String senha,

        @NotBlank(message = "Obrigatório fornecer o e-mail")
        @Email(message = "Formato de e-mail inválido.")
        @Size(max = 254, message = "O email deve ter no máximo 254 caracteres.")
        String email,

        @NotBlank(message = "Obrigatório fornecer o celular")
        @Size(max = 20, message = "O número de celular deve conter no máximo 20 dígitos.")
        String celular

) {



}
