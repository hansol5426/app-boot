package it.korea.app_boot.common.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        String message = 
            e.getMessage() != null && e.getMessage().length() > 0 ? e.getMessage() : "서버에 오류가 있습니다.";
        ErrorResponse err = new ErrorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(Exception e){
        String message = 
            e.getMessage() != null && e.getMessage().length() > 0 ? e.getMessage() : "서버에 오류가 있습니다.";
        ErrorResponse err = new ErrorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
}
