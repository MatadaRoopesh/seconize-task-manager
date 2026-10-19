
package com.seconize.taskmanager.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 1. Handle request body validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, Object> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> response = Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Validation Failed",
                "messages", errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    // 2. Handle invalid parameter types and enum values
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put(
                "message",
                "Invalid value for parameter: " + ex.getName()
        );

        if (ex.getRequiredType() != null
                && ex.getRequiredType().isEnum()) {

            Object[] validValues =
                    ex.getRequiredType().getEnumConstants();

            body.put(
                    "validValues",
                    Arrays.stream(validValues)
                            .map(Object::toString)
                            .toList()
            );
        }

        return ResponseEntity.badRequest().body(body);
    }

    // 3. Handle resource not found and HTTP status exceptions
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>>
    handleResponseStatusException(ResponseStatusException ex) {

        HttpStatus status = HttpStatus.valueOf(
                ex.getStatusCode().value()
        );

        Map<String, Object> response = Map.of(
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", ex.getReason() != null
                        ? ex.getReason()
                        : "Request failed"
        );

        return ResponseEntity.status(status).body(response);
    }

    // 4. Handle invalid JSON, dates, and enum values
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>>
    handleInvalidRequestBody(HttpMessageNotReadableException ex) {

        String message = "Invalid request body or date format";

        Throwable cause = ex.getMostSpecificCause();
        String causeMessage = cause.getMessage();

        if (causeMessage != null
                && causeMessage.contains("ProjectStatus")) {

            message = "Invalid project status. Valid values: "
                    + "PLANNING, IN_PROGRESS, ON_HOLD, COMPLETED, CANCELLED";

        } else if (causeMessage != null
                && causeMessage.contains("TaskStatus")) {

            message = "Invalid task status. Valid values: "
                    + "TODO, IN_PROGRESS, BLOCKED, DONE";
        }

        Map<String, Object> response = Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Bad Request",
                "message", message
        );

        return ResponseEntity.badRequest().body(response);
    }

    // 5. Handle persistence constraint violations
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>>
    handleConstraintViolation(ConstraintViolationException ex) {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put(
                "status",
                HttpStatus.BAD_REQUEST.value()
        );
        response.put("error", "Validation Failed");

        response.put(
                "messages",
                ex.getConstraintViolations()
                        .stream()
                        .map(violation -> violation.getMessage())
                        .toList()
        );

        return ResponseEntity.badRequest().body(response);
    }

    // 6. Handle unsupported HTTP methods
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>>
    handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex) {

        Map<String, Object> response = Map.of(
                "status", HttpStatus.METHOD_NOT_ALLOWED.value(),
                "error", "Method Not Allowed",
                "message", "HTTP method " + ex.getMethod()
                        + " is not supported for this endpoint"
        );

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(response);
    }

    // 7. Handle unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>>
    handleUnexpectedException(Exception ex) {

        logger.error("Unexpected error occurred", ex);

        Map<String, Object> response = Map.of(
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "error", "Internal Server Error",
                "message", "An unexpected error occurred"
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
