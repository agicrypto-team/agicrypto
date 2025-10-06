package com.devsdoagi.agicripto.exception.historicoCriptomoedas;

public class ConversaoMoedaException extends RuntimeException {
    public ConversaoMoedaException(String message) {
        super(message);
    }

    public ConversaoMoedaException(String message, Throwable cause) {
        super(message, cause);
    }
}
