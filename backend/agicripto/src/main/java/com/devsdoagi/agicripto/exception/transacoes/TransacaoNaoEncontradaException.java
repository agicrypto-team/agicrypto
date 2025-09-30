package com.devsdoagi.agicripto.exception.transacoes;

public class TransacaoNaoEncontradaException extends RuntimeException {
    public TransacaoNaoEncontradaException(Integer id) {
        super("Transação com id " + id + " não encontrada.");
    }
}

