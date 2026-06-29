package br.com.sistemacalculosjudiciais.repository;

import br.com.sistemacalculosjudiciais.model.IndiceEconomico;
import br.com.sistemacalculosjudiciais.model.TipoIndice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

// Repositório JPA para acesso e persistência dos índices econômicos no banco de dados
public interface IndiceEconomicoRepository extends JpaRepository<IndiceEconomico, Long> {

    List<IndiceEconomico> findByTipoIndiceAndCompetenciaBetweenOrderByCompetenciaAsc(TipoIndice tipoIndice,
                                                                                     LocalDate competenciaInicial,
                                                                                     LocalDate competenciaFinal);

    boolean existsByTipoIndiceAndCompetencia(TipoIndice tipoIndice, LocalDate competencia);

    long countByTipoIndice(TipoIndice tipoIndice);

    void deleteByTipoIndice(TipoIndice tipoIndice);
}