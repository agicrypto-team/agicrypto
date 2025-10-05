package com.devsdoagi.agicripto.exception.usuarios;

public class ExistingUserException extends RuntimeException {
    public ExistingUserException(String message) {
        super(message);
    }
}
