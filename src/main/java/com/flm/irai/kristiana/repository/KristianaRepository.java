package com.flm.irai.kristiana.repository;

import com.flm.irai.kristiana.model.Kristiana;
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
public interface KristianaRepository extends JpaRepository<Kristiana, UUID> {

    // Recherche par matricule (unique)
    Optional<Kristiana> findByMatricule(String matricule);

    // Verification d'existence par matricule
    boolean existsByMatricule(String matricule);

    // Verification d'existence par matricule (excluant un id)
    @Query("SELECT CASE WHEN COUNT(k) > 0 THEN true ELSE false END FROM Kristiana k " +
           "WHERE k.matricule = :matricule AND k.id != :excludeId")
    boolean existsByMatriculeExcludingId(@Param("matricule") String matricule, @Param("excludeId") UUID excludeId);

    // Recherche par nom (partiel, insensible a la casse)
    Page<Kristiana> findByNomContainingIgnoreCase(String nom, Pageable pageable);

    // Recherche par prenom (partiel, insensible a la casse)
    Page<Kristiana> findByPrenomContainingIgnoreCase(String prenom, Pageable pageable);

    // Recherche par nom et prenom
    Page<Kristiana> findByNomContainingIgnoreCaseAndPrenomContainingIgnoreCase(
            String nom, String prenom, Pageable pageable);

    // Recherche des enfants d'un pere
    List<Kristiana> findByPereBiologiqueId(UUID pereId);

    // Recherche des enfants d'une mere
    List<Kristiana> findByMereBiologiqueId(UUID mereId);

    // Recherche avec filtres optionnels
    @Query("SELECT k FROM Kristiana k WHERE " +
           "(COALESCE(:nom, '') = '' OR LOWER(k.nom) LIKE LOWER(CONCAT('%', CAST(:nom AS string), '%'))) AND " +
           "(COALESCE(:prenom, '') = '' OR LOWER(k.prenom) LIKE LOWER(CONCAT('%', CAST(:prenom AS string), '%'))) AND " +
           "(COALESCE(:matricule, '') = '' OR LOWER(k.matricule) LIKE LOWER(CONCAT('%', CAST(:matricule AS string), '%'))) AND " +
           "(:sexe IS NULL OR k.sexe = :sexe)")
    Page<Kristiana> findWithFilters(
            @Param("nom") String nom,
            @Param("prenom") String prenom,
            @Param("matricule") String matricule,
            @Param("sexe") String sexe,
            Pageable pageable
    );

    // Recherche globale (nom, prenom ou matricule)
    @Query("SELECT k FROM Kristiana k WHERE " +
           "LOWER(k.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(k.prenom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(k.matricule) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Kristiana> searchGlobal(@Param("search") String search, Pageable pageable);

    // Compte les kristiana par sexe
    long countBySexe(String sexe);
}
