package com.devsdoagi.agicripto.exception.Carteiras;

public class CarteiraNaoEncontradaException extends RuntimeException {

    public CarteiraNaoEncontradaException(Integer id) {
        super("Carteira com ID " + id + " não encontrada.");
    }
}
