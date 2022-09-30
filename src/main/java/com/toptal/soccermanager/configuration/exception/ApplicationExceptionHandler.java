package com.toptal.soccermanager.configuration.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApplicationErrorResponse> handleConflict(ApplicationException ex) {
        ApplicationError applicationError = ex.getApplicationError();

        ApplicationErrorResponse errorResponse = new ApplicationErrorResponse();
        errorResponse.setStatus(applicationError.getHttpStatus().name());
        errorResponse.setDescription(applicationError.getDescription());
        errorResponse.setMessage(ex.getAdditionalMessage());
        errorResponse.setError(applicationError.name());

        return new ResponseEntity<>(errorResponse, applicationError.getHttpStatus());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, ConstraintViolationException.class})
    public ResponseEntity<ApplicationErrorResponse> handleValidationException(Exception ex) {
        ApplicationErrorResponse errorResponse = new ApplicationErrorResponse();

        errorResponse.setStatus(HttpStatus.BAD_REQUEST.name());
        errorResponse.setDescription(ex.getMessage());
        errorResponse.setError("ValidationError");

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                          HttpStatus status, WebRequest request) {
        return handleFieldValidationError(ex);
    }

    @Override
    protected ResponseEntity handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        return handleFieldValidationError(Collections.singletonList(new ApplicationErrorResponse.FieldError(ex.getParameterName(), "")));
    }

    @Override
    protected ResponseEntity handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleFieldValidationError(ex);
    }

    @Override
    protected ResponseEntity handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        return buildErrorResponse(ex, ex.getMessage(), status);
    }

    private ResponseEntity<ApplicationErrorResponse> handleFieldValidationError(List<ApplicationErrorResponse.FieldError> errorFields) {
        ApplicationErrorResponse errorResponse = new ApplicationErrorResponse();

        ApplicationError applicationError = ApplicationError.REQUEST_PARAMETERS_NOT_VALID;
        errorResponse.setStatus(applicationError.getHttpStatus().name());
        errorResponse.setDescription(applicationError.getDescription());
        errorResponse.setError(applicationError.name());
        errorResponse.setErrorFields(errorFields);

        return new ResponseEntity<>(errorResponse, applicationError.getHttpStatus());
    }

    private ResponseEntity<ApplicationErrorResponse> handleFieldValidationError(BindException ex) {
        return handleFieldValidationError(
                ex.getBindingResult().getFieldErrors().stream()
                        .map(e -> new ApplicationErrorResponse.FieldError(e.getField(), e.getDefaultMessage()))
                        .collect(Collectors.toList())
        );
    }

    private ResponseEntity buildErrorResponse(Exception ex, String error, HttpStatus status) {
        return new ResponseEntity<>(error, status);
    }
}
