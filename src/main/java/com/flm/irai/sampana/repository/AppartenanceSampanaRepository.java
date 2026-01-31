package com.flm.irai.sampana.repository;

import com.flm.irai.sampana.model.AppartenanceSampana;
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
public interface AppartenanceSampanaRepository extends JpaRepository<AppartenanceSampana, UUID> {

    // Recherche par Kristiana
    List<AppartenanceSampana> findByKristianaId(UUID kristianaId);

    Page<AppartenanceSampana> findByKristianaId(UUID kristianaId, Pageable pageable);

    // Recherche par Sampana
    List<AppartenanceSampana> findBySampanaId(UUID sampanaId);

    Page<AppartenanceSampana> findBySampanaId(UUID sampanaId, Pageable pageable);

    // Recherche les appartenances actives d'un Kristiana
    List<AppartenanceSampana> findByKristianaIdAndEstActifTrue(UUID kristianaId);

    // Recherche les membres actifs d'un Sampana
    List<AppartenanceSampana> findBySampanaIdAndEstActifTrue(UUID sampanaId);

    Page<AppartenanceSampana> findBySampanaIdAndEstActifTrue(UUID sampanaId, Pageable pageable);

    // Verifie si un Kristiana est deja membre actif d'un Sampana
    boolean existsByKristianaIdAndSampanaIdAndEstActifTrue(UUID kristianaId, UUID sampanaId);

    // Recherche l'appartenance active d'un Kristiana a un Sampana
    Optional<AppartenanceSampana> findByKristianaIdAndSampanaIdAndEstActifTrue(UUID kristianaId, UUID sampanaId);

    // Compte les membres actifs d'un Sampana
    long countBySampanaIdAndEstActifTrue(UUID sampanaId);

    // Compte les Sampana auxquels un Kristiana appartient activement
    long countByKristianaIdAndEstActifTrue(UUID kristianaId);

    // Recherche avec filtres
    @Query("SELECT a FROM AppartenanceSampana a WHERE " +
           "(:sampanaId IS NULL OR a.sampana.id = :sampanaId) AND " +
           "(:kristianaId IS NULL OR a.kristiana.id = :kristianaId) AND " +
           "(:estActif IS NULL OR a.estActif = :estActif)")
    Page<AppartenanceSampana> findWithFilters(
            @Param("sampanaId") UUID sampanaId,
            @Param("kristianaId") UUID kristianaId,
            @Param("estActif") Boolean estActif,
            Pageable pageable);
}
