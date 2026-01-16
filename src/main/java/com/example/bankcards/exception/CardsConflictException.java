package com.example.bankcards.exception;

public class CardsConflictException extends RuntimeException {
    public CardsConflictException(String message) {
        super(message);
    }
}
