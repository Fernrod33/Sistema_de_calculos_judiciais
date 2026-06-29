package br.com.sistemacalculosjudiciais.repository;

import br.com.sistemacalculosjudiciais.model.Calculo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repositório JPA para acesso e persistência dos cálculos no banco de dados
public interface CalculoRepository extends JpaRepository<Calculo, Long> {

    @EntityGraph(attributePaths = "memoria")
    List<Calculo> findAllByOrderByDataCriacaoDesc();
}