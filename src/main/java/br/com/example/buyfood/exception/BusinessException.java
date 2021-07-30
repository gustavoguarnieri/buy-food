package br.com.example.buyfood.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
  private HttpStatus statusCode;

  public BusinessException(String message) {
    super(message);
  }

  public BusinessException(String message, Throwable throwable) {
    super(message, throwable);
  }

  public BusinessException(HttpStatus statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }
}
