package com.devsdoagi.agicripto.exception;

import com.devsdoagi.agicripto.DTO.ApiError;
import com.devsdoagi.agicripto.exception.ExistingUserException;
import com.devsdoagi.agicripto.exception.historicoCriptomoedas.HistoricoCriptomoedaNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExistingUserException.class)
    public ResponseEntity<String> handleExistingUserException(ExistingUserException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    /*
    @ExceptionHandler(AutenticacaoException.class)
    public ResponseEntity<String> handleAutenticacaoException(AutenticacaoException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    */

    /* Handlers referentes a HistoricoCriptomoedas */

    @ExceptionHandler(HistoricoCriptomoedaNaoEncontradoException.class)
    public ResponseEntity<String> handleHistoricoCriptomoedaNaoEncontradoException(HistoricoCriptomoedaNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /*
    @ExceptionHandler(HistoricoCriptomoedaNaoEncontradoException.class)
    public ResponseEntity<ApiError> handleNotFound(HistoricoCriptomoedaNaoEncontradoException ex, HttpServletRequest req) {

        ApiError body = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                req.getRequestURI(),
                LocalDateTime.now().toString()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    */

    /* Handler Genérico */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro interno no servidor.");
    }
}