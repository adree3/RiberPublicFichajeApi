package com.example.riberpublicfichajeapi.excepciones;

public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String msg) { super(msg); }
}
