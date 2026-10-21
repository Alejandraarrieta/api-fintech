package com.fintech.apifintech.repository;

import com.fintech.apifintech.model.Cuenta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    @Override
    @EntityGraph(attributePaths = "usuario")
    List<Cuenta> findAll();

    @Override
    @EntityGraph(attributePaths = "usuario")
    Optional<Cuenta> findById(Long id);

    boolean existsByNumero(String numero);
    boolean existsByNumeroAndIdNot(String numero, Long id);
}
