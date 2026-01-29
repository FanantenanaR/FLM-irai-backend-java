package com.flm.irai.utilisateur.repository;

import com.flm.irai.utilisateur.model.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, UUID> {

    // Recherche par email (unique)
    Optional<Utilisateur> findByEmail(String email);

    // Verification d'existence par email
    boolean existsByEmail(String email);

    // Verification d'existence par email (excluant un id)
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Utilisateur u " +
           "WHERE u.email = :email AND u.id != :excludeId")
    boolean existsByEmailExcludingId(@Param("email") String email, @Param("excludeId") UUID excludeId);

    // Recherche par kristiana_id
    Optional<Utilisateur> findByKristianaId(UUID kristianaId);

    // Verification d'existence par kristiana_id
    boolean existsByKristianaId(UUID kristianaId);

    // Recherche des utilisateurs actifs
    Page<Utilisateur> findByEstActif(Boolean estActif, Pageable pageable);

    // Recherche par email (partiel, insensible a la casse)
    Page<Utilisateur> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    // Recherche avec filtres optionnels
    @Query("SELECT u FROM Utilisateur u WHERE " +
           "(:estActif IS NULL OR u.estActif = :estActif) AND " +
           "(COALESCE(:email, '') = '' OR LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:email AS string), '%')))")
    Page<Utilisateur> findWithFilters(
            @Param("estActif") Boolean estActif,
            @Param("email") String email,
            Pageable pageable
    );
}
