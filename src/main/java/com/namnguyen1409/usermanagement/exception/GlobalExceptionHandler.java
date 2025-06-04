package com.namnguyen1409.usermanagement.exception;

import com.namnguyen1409.usermanagement.dto.response.CustomApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<CustomApiResponse<Void>> handlingRuntimeException(
            Exception exception
    ) {
        CustomApiResponse<Void> customApiResponse = new CustomApiResponse<>();

        customApiResponse.setCode(ErrorCode.UNCATEGORIZED.getCode());
        customApiResponse.setMessage(ErrorCode.UNCATEGORIZED.getMessage());
        log.error("Unhandled exception {}", (Object) exception.getStackTrace());
        return ResponseEntity.badRequest().body(customApiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ResponseEntity<CustomApiResponse<Object>> handlingAccessDeniedException() {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity.status(errorCode.getStatusCode())
                .body(CustomApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<CustomApiResponse<Object>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(CustomApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(value = JwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ResponseEntity<CustomApiResponse<Object>> handlingJwtException(JwtException exception) {
        ErrorCode errorCode = ErrorCode.INVALID_TOKEN;

        return ResponseEntity.status(errorCode.getStatusCode())
                .body(CustomApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }


    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<CustomApiResponse<Object>> handleBindException(BindException e) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });

        CustomApiResponse<Object> response = CustomApiResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed")
                .data(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
