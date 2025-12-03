package com.example.diy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;



public class ValidationExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ValidationExceptionHandler.class);

    /**
     * Handles validation exceptions thrown when an argument annotated with @Valid fails.
     * Returns a 400 Bad Request with a map of field errors.
     *
     * @param ex The MethodArgumentNotValidException instance.
     * @return A ResponseEntity with a map of field-error messages and HttpStatus.BAD_REQUEST.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidationExceptions(MethodArgumentNotValidException ex){
        Map<String,String> errors=new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error->
                errors.put(error.getField(),error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    /**
     * Catches all other uncaught exceptions (the global fallback handler).
     * Logs the full error stack trace and returns a generic 500 Internal Server Error
     * message to the client for security.
     *
     * @param ex The uncaught Exception instance.
     * @return A ResponseEntity with a generic error message and HttpStatus.INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralExceptions(Exception ex) {
        // ✅ לוג חובה: רישום מלא של השגיאה לשרת
        logger.error("Global Exception Handler caught an unexpected error: ", ex);
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "An unexpected internal server error occurred.");
        return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }
   }
