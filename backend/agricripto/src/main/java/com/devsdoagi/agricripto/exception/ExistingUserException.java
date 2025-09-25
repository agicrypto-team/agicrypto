package com.devsdoagi.agricripto.expection;

public class ExistingUserException extends RuntimeExpection {
    public ExistingUserException(String message) {
        super(message);
    }
}
