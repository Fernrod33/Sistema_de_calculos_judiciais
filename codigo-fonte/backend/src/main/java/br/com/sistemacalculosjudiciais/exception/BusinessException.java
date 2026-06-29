package br.com.sistemacalculosjudiciais.exception;

// Exceção lançada para violações de regras de negócio da aplicação
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}