package com.example.backend.repository;

import com.example.backend.domain.BeneficioEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BeneficioRepository extends JpaRepository<BeneficioEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from BeneficioEntity b where b.id = :id")
    Optional<BeneficioEntity> findByIdForUpdate(@Param("id") Long id);
}