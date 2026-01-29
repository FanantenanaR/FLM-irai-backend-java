package com.flm.irai.faritra.repository;

import com.flm.irai.faritra.model.Faritra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FaritraRepository extends JpaRepository<Faritra, UUID> {

    // Recherche par fiangonana
    List<Faritra> findByFiangonanaId(UUID fiangonanaId);

    // Recherche par fiangonana avec pagination
    Page<Faritra> findByFiangonanaId(UUID fiangonanaId, Pageable pageable);

    // Recherche par nom (partiel, insensible a la casse)
    Page<Faritra> findByNomContainingIgnoreCase(String nom, Pageable pageable);

    // Recherche par nom et fiangonana
    Page<Faritra> findByNomContainingIgnoreCaseAndFiangonanaId(String nom, UUID fiangonanaId, Pageable pageable);

    // Verification d'existence par nom dans une fiangonana
    boolean existsByNomAndFiangonanaId(String nom, UUID fiangonanaId);

    // Compte les faritra d'une fiangonana
    long countByFiangonanaId(UUID fiangonanaId);

    // Recherche avec filtres optionnels
    @Query("SELECT f FROM Faritra f WHERE " +
           "(:fiangonanaId IS NULL OR f.fiangonana.id = :fiangonanaId) AND " +
           "(:nom IS NULL OR LOWER(f.nom) LIKE LOWER(CONCAT('%', :nom, '%')))")
    Page<Faritra> findWithFilters(
            @Param("fiangonanaId") UUID fiangonanaId,
            @Param("nom") String nom,
            Pageable pageable
    );
}
