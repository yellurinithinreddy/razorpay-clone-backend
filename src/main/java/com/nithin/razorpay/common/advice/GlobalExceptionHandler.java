package com.nithin.razorpay.common.advice;

import com.nithin.razorpay.common.exceptions.DuplicateResourceException;
import com.nithin.razorpay.common.exceptions.RateLimitException;
import com.nithin.razorpay.common.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> duplicateResourceExceptionHandler(DuplicateResourceException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.of(ex.getErrorCode(),ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundExceptionHandler(ResourceNotFoundException ex){
        String errorCode = ex.getResourceName().toUpperCase()+"_NOT_FOUND";
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.of(errorCode,ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex){
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ErrorResponse.FieldError(fe.getField(),fe.getDefaultMessage()))
                .toList();



        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.of("VALIDATION_FAILED",
                "Request Validation Failed",fieldErrors));
    }

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ErrorResponse> rateLimitExceptionHandler(RateLimitException ex){
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-RateLimit-Remaining","0")
                .header("Retry-After",String.valueOf(ex.getRetryAfterSeconds()))
                .header("X-RateLimit-Reset",
                        String.valueOf(Instant.now().plusSeconds(ex.getRetryAfterSeconds()).getEpochSecond()
                        ))
                .body(ErrorResponse.of("RATE_LIMIT_EXCEEDED",ex.getMessage()));
    }
}
