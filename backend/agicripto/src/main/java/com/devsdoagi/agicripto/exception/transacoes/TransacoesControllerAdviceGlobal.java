package com.devsdoagi.agicripto.exception.transacoes;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// Arquivo que "traduz as exeções para a API possibilitando que as mensagens sejam exibidas no Postman - Rafael"
@ControllerAdvice
public class TransacoesControllerAdviceGlobal {

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<String> handleUsuarioNaoEncontrado(UsuarioNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(CriptomoedaNaoEncontradaException.class)
    public ResponseEntity<String> handleCriptomoedaNaoEncontrada(CriptomoedaNaoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(TransacaoNaoEncontradaException.class)
    public ResponseEntity<String> handleTransacaoNaoEncontrada(TransacaoNaoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
