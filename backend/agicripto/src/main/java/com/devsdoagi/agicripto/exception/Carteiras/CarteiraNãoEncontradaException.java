package com.devsdoagi.agicripto.exception.Carteiras;

public class CarteiraNãoEncontradaException extends RuntimeException {

    public CarteiraNãoEncontradaException(Integer id) {
        super("Carteira com ID " + id + " não encontrada.");
    }
}
