package br.com.example.buyfood.api;

import br.com.example.buyfood.exception.ApiException;
import br.com.example.buyfood.exception.BusinessException;
import br.com.example.buyfood.exception.ConflitException;
import br.com.example.buyfood.exception.FileNotFoundException;
import br.com.example.buyfood.exception.NotFoundException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import javax.ws.rs.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

  private MessageSource messageSource;

  @Autowired
  public ApiExceptionHandler(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<Object> handleApi(ApiException ex, WebRequest request) {
    var status = HttpStatus.INTERNAL_SERVER_ERROR;
    var error = new Error(status.value(), ex.getMessage(), OffsetDateTime.now(), null);
    return handleExceptionInternal(ex, error, new HttpHeaders(), status, request);
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<Object> handleBusiness(BusinessException ex, WebRequest request) {
    var status = HttpStatus.BAD_REQUEST;
    var error = new Error(status.value(), ex.getMessage(), OffsetDateTime.now(), null);
    return handleExceptionInternal(ex, error, new HttpHeaders(), status, request);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<Object> handleNotFound(NotFoundException ex, WebRequest request) {
    var status = HttpStatus.NOT_FOUND;
    var error = new Error(status.value(), ex.getMessage(), OffsetDateTime.now(), null);
    return handleExceptionInternal(ex, error, new HttpHeaders(), status, request);
  }

  @ExceptionHandler(FileNotFoundException.class)
  public ResponseEntity<Object> handleFileNotFound(FileNotFoundException ex, WebRequest request) {
    var status = HttpStatus.NOT_FOUND;
    var error = new Error(status.value(), ex.getMessage(), OffsetDateTime.now(), null);
    return handleExceptionInternal(ex, error, new HttpHeaders(), status, request);
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<Object> handleForbidden(ForbiddenException ex, WebRequest request) {
    var status = HttpStatus.FORBIDDEN;
    var error = new Error(status.value(), ex.getMessage(), OffsetDateTime.now(), null);
    return handleExceptionInternal(ex, error, new HttpHeaders(), status, request);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
    var status = HttpStatus.FORBIDDEN;
    var error = new Error(status.value(), ex.getMessage(), OffsetDateTime.now(), null);
    return handleExceptionInternal(ex, error, new HttpHeaders(), status, request);
  }

  @ExceptionHandler(ConflitException.class)
  public ResponseEntity<Object> handleConflit(ConflitException ex, WebRequest request) {
    var status = HttpStatus.CONFLICT;
    var error = new Error(status.value(), ex.getMessage(), OffsetDateTime.now(), null);
    return handleExceptionInternal(ex, error, new HttpHeaders(), status, request);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Object> handleIllegalArgument(
      IllegalArgumentException ex, WebRequest request) {
    var status = HttpStatus.BAD_REQUEST;
    var error = new Error(status.value(), ex.getMessage(), OffsetDateTime.now(), null);
    return handleExceptionInternal(ex, error, new HttpHeaders(), status, request);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {

    var fields = new ArrayList<Error.Field>();

    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String name = ((FieldError) error).getField();
              String message = messageSource.getMessage(error, LocaleContextHolder.getLocale());
              fields.add(new Error.Field(name, message));
            });

    var error = new Error(status.value(), "Please check the fields", OffsetDateTime.now(), fields);

    return super.handleExceptionInternal(ex, error, headers, status, request);
  }
}
