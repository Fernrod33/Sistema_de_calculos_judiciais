package br.com.sistemacalculosjudiciais.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

// Entidade JPA que representa um índice econômico mensal (SELIC, IPCA ou IGP-M) armazenado no banco
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "indice_economico")
public class IndiceEconomico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_indice", nullable = false)
    private TipoIndice tipoIndice;

    @Column(nullable = false)
    private LocalDate competencia;

    @Column(name = "valor_percentual", nullable = false, precision = 19, scale = 8)
    private BigDecimal valorPercentual;

    @Column(name = "data_importacao", nullable = false)
    private LocalDate dataImportacao;

    @Column(nullable = false, length = 120)
    private String fonte;
}