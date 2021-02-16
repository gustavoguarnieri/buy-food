package br.com.example.buyfood.exception;

public class NotFoundException extends BusinessException {
    public NotFoundException(String message) {
        super(message.isBlank() ? "No data found" : message);
    }
}
