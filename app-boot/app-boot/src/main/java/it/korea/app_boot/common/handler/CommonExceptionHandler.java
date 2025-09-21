package it.korea.app_boot.common.handler;

import java.nio.file.AccessDeniedException;

import javax.naming.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import it.korea.app_boot.common.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;

// RestController가 에러났을 때 영향을 줌
// controller는 responseBody 부분에 영향을 줌
@RestControllerAdvice
@RequiredArgsConstructor
public class CommonExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e){

        if(isSecurityException(e)){
            throw (RuntimeException) e;  // Security 예외는 다 던짐
        }

        String message = 
            e.getMessage() != null && e.getMessage().length() > 0 ? e.getMessage() : "서버에 오류가 있습니다.";
        ErrorResponse err = new ErrorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(Exception e){

        if(isSecurityException(e)){
            throw (RuntimeException) e;  // Security 예외는 다 던짐
        }

        String message = 
            e.getMessage() != null && e.getMessage().length() > 0 ? e.getMessage() : "서버에 오류가 있습니다.";
        ErrorResponse err = new ErrorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }

    private boolean isSecurityException(Exception e){
        return e instanceof AuthenticationException ||
               e instanceof AccessDeniedException ||
               e instanceof AuthenticationCredentialsNotFoundException;
    }
}
