package com.devsdoagi.agicripto.exception.transacoes;

public class CriptomoedaNaoEncontradaException extends RuntimeException {
    public CriptomoedaNaoEncontradaException(Integer id) {
        super("Criptomoeda com id " + id + " não encontrada.");
    }
}
