package br.com.sistemacalculosjudiciais.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Classe utilitária com métodos de apoio para operações matemáticas dos cálculos judiciais
public final class CalculoUtils {

    private CalculoUtils() {
    }

    public static BigDecimal aplicarIndice(BigDecimal valor, BigDecimal percentual) {
        BigDecimal fator = percentual.divide(BigDecimal.valueOf(100), 16, RoundingMode.HALF_EVEN)
                .add(BigDecimal.ONE);
        return valor.multiply(fator).setScale(8, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal aplicarIndiceReverso(BigDecimal valor, BigDecimal percentual) {
        BigDecimal fator = percentual.divide(BigDecimal.valueOf(100), 16, RoundingMode.HALF_EVEN)
                .add(BigDecimal.ONE);
        return valor.divide(fator, 8, RoundingMode.HALF_EVEN);
    }
}