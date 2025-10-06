package com.devsdoagi.agicripto.exception.criptomoedas;

public class ResponsavelNaoEncontradoException extends RuntimeException {
    public ResponsavelNaoEncontradoException(Integer id) {
        super("O responsável com ID " + id + " não foi encontrado.");
    }
}
