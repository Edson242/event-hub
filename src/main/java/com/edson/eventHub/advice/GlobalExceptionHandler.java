package com.edson.eventHub.advice;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

/**
 * Handler de Exceções Global para a aplicação.
 * Captura exceções específicas e genéricas, retornando respostas HTTP padronizadas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Captura erros de validação de beans (anotações como @Valid).
     * @param ex A exceção lançada.
     * @return Um mapa contendo os campos e as mensagens de erro.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        logger.warn("Erro de validação de dados: {}", errors);
        return errors;
    }

    /**
     * Captura exceções quando uma entidade não é encontrada no banco de dados.
     * @param ex A exceção lançada.
     * @return Uma resposta com status 404 (Not Found) e a mensagem de erro.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(EntityNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Recurso não encontrado.");
        error.put("message", ex.getMessage());
        logger.warn("Tentativa de acesso a recurso inexistente: {}", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Captura exceções genéricas não tratadas especificamente.
     * @param ex A exceção lançada.
     * @return Uma resposta com status 500 (Internal Server Error) e uma mensagem genérica.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralExceptions(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Ocorreu um erro inesperado no servidor.");
        error.put("message", ex.getMessage());
        logger.error("Erro inesperado na aplicação: ", ex);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}