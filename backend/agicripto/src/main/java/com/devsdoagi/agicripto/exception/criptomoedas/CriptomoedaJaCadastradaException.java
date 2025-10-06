package com.devsdoagi.agicripto.exception.criptomoedas;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CriptomoedaJaCadastradaException extends RuntimeException {
    public CriptomoedaJaCadastradaException(String sigla) {
        super("A criptomoeda com a sigla '" + sigla + "' já está cadastrada.");
    }
}
