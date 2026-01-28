package com.flm.irai.organisation.repository;

import com.flm.irai.organisation.model.TypeOrganisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TypeOrganisationRepository extends JpaRepository<TypeOrganisation, UUID> {

    // Recherche par code (unique)
    Optional<TypeOrganisation> findByCode(String code);

    // Verification d'existence par code
    boolean existsByCode(String code);
}
