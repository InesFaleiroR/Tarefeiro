package com.tarefeiro.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " não encontrado com ID: " + id);
    }
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
