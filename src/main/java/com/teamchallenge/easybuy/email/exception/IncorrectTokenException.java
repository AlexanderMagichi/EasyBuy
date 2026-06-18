package com.teamchallenge.easybuy.email.exception;

public class IncorrectTokenException extends RuntimeException {

    public IncorrectTokenException() {
        super("Incorrect token");
    }
}
