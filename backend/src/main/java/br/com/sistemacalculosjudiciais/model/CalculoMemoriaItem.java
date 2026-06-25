package br.com.sistemacalculosjudiciais.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.YearMonth;

// Entidade JPA que representa um item da memória de cálculo (valor corrigido por competência)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "calculo_memoria")
public class CalculoMemoriaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calculo_id", nullable = false)
    private Calculo calculo;

    @Column(nullable = false)
    private YearMonth competencia;

    @Column(name = "indice_percentual", nullable = false, precision = 19, scale = 8)
    private BigDecimal indicePercentual;

    @Column(name = "valor_anterior", nullable = false, precision = 19, scale = 8)
    private BigDecimal valorAnterior;

    @Column(name = "valor_corrigido", nullable = false, precision = 19, scale = 8)
    private BigDecimal valorCorrigido;
}