package com.fintech.apifintech.exception;

public class ConflictoException extends RuntimeException {
    public ConflictoException(String message) { super(message); }
    public ConflictoException(String message, Throwable cause) { super(message, cause); }
}
