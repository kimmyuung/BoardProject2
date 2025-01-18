package com.kimmyungho.board.exception;


import com.kimmyungho.board.model.error.ClientErrorResponse;
import com.kimmyungho.board.model.error.ServerErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClientErrorException.class)
    public ResponseEntity<ClientErrorResponse> handleClientErrorException(ClientErrorException e) {
        return new ResponseEntity<>(new ClientErrorResponse(
                e.getStatus(), e.getMessage()), e.getStatus());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ClientErrorResponse> handleClientErrorException(HttpMessageNotReadableException e) {
        return new ResponseEntity<>(new ClientErrorResponse(
                HttpStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ClientErrorResponse> handleClientErrorException(MethodArgumentNotValidException e) {
        // 필드 에러 메시지를 쉼표로 구분된 문자열로 변환
        var errorMessage = e.getFieldErrors().stream()
                .map(fieldError -> "%s: %s".formatted(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.joining(", "));

        // ClientErrorResponse 객체 생성 및 반환
        var response = new ClientErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ServerErrorResponse> handleClientErrorException(RuntimeException e) {
        // return ResponseEntity.internalServerError().build(); 대체 가능
        return new ResponseEntity<>(new ServerErrorResponse( HttpStatus.INTERNAL_SERVER_ERROR ).status() ) ;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ServerErrorResponse> handleClientErrorException(Exception e) {
        return new ResponseEntity<>(new ServerErrorResponse( HttpStatus.INTERNAL_SERVER_ERROR ).status() );
    }
}
