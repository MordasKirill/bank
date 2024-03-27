package org.international.bank.exception;

public class ValidationException extends Exception{

    public ValidationException(String errorMessage) {
        super(errorMessage);
    }
}
