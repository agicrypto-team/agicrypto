package com.devsdoagi.agicripto.exception.transacoes;

public class UsuarioNaoEncontradoException extends RuntimeException {
    public UsuarioNaoEncontradoException(Integer id) {
        super("Usuário com id " + id + " não encontrado.");
    }
}
