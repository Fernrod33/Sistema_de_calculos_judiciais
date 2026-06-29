package br.com.sistemacalculosjudiciais.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Entidade JPA que representa um cálculo judicial armazenado no banco de dados
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "calculo")
public class Calculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_calculo", nullable = false)
    private TipoCalculo tipoCalculo;

    @Enumerated(EnumType.STRING)
    @Column(name = "indice_utilizado", nullable = false)
    private TipoIndice indiceUtilizado;

    @Column(name = "valor_original", nullable = false, precision = 19, scale = 8)
    private BigDecimal valorOriginal;

    @Column(name = "valor_final", nullable = false, precision = 19, scale = 8)
    private BigDecimal valorFinal;

    @Column(name = "data_inicial", nullable = false)
    private LocalDate dataInicial;

    @Column(name = "data_final", nullable = false)
    private LocalDate dataFinal;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "nome_calculo", length = 120)
    private String nomeCalculo;

    @OneToMany(mappedBy = "calculo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CalculoPeriodo> periodos = new ArrayList<>();

    @OneToMany(mappedBy = "calculo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CalculoMemoriaItem> memoria = new ArrayList<>();
}