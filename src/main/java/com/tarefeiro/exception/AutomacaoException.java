package com.tarefeiro.exception;

public class AutomacaoException extends RuntimeException {
    public AutomacaoException(String message) { super(message); }
    public AutomacaoException(String message, Throwable cause) { super(message, cause); }
}
