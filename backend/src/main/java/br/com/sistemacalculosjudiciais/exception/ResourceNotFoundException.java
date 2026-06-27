package br.com.sistemacalculosjudiciais.exception;

// Exceção lançada quando um recurso solicitado (cálculo, índice) não é encontrado no banco de dados
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}