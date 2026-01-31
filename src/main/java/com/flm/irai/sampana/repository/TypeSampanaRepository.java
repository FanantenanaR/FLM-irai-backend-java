package com.flm.irai.sampana.repository;

import com.flm.irai.sampana.model.TypeSampana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TypeSampanaRepository extends JpaRepository<TypeSampana, UUID> {

    Optional<TypeSampana> findByCode(String code);

    boolean existsByCode(String code);
}
