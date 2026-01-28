package com.flm.irai.organisation.repository;

import com.flm.irai.organisation.model.Organisation;
import com.flm.irai.organisation.model.TypeOrganisation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, UUID> {

    // Recherche par code (unique)
    Optional<Organisation> findByCode(String code);

    // Verification d'existence par code
    boolean existsByCode(String code);

    // Recherche par type
    List<Organisation> findByType(TypeOrganisation type);

    // Recherche par type (via id)
    List<Organisation> findByTypeId(UUID typeId);

    // Recherche par type avec pagination
    Page<Organisation> findByTypeId(UUID typeId, Pageable pageable);

    // Recherche par parent
    List<Organisation> findByParentId(UUID parentId);

    // Recherche par parent avec pagination
    Page<Organisation> findByParentId(UUID parentId, Pageable pageable);

    // Recherche par type et parent avec pagination
    Page<Organisation> findByTypeIdAndParentId(UUID typeId, UUID parentId, Pageable pageable);

    // Recherche des organisations racines (sans parent)
    List<Organisation> findByParentIsNull();

    // Recherche avec filtres optionnels
    @Query("SELECT o FROM Organisation o WHERE " +
           "(:typeId IS NULL OR o.type.id = :typeId) AND " +
           "(:parentId IS NULL OR o.parent.id = :parentId)")
    Page<Organisation> findWithFilters(
            @Param("typeId") UUID typeId,
            @Param("parentId") UUID parentId,
            Pageable pageable
    );

    // Recherche par nom (partiel, insensible a la casse)
    Page<Organisation> findByNomContainingIgnoreCase(String nom, Pageable pageable);

    // Compte les enfants d'une organisation
    long countByParentId(UUID parentId);

    // Recherche par code du type
    @Query("SELECT o FROM Organisation o WHERE o.type.code = :typeCode")
    List<Organisation> findByTypeCode(@Param("typeCode") String typeCode);

    // Recherche par code du type avec pagination
    @Query("SELECT o FROM Organisation o WHERE o.type.code = :typeCode")
    Page<Organisation> findByTypeCode(@Param("typeCode") String typeCode, Pageable pageable);
}
