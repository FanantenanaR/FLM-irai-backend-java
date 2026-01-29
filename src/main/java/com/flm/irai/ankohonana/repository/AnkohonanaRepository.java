package com.flm.irai.ankohonana.repository;

import com.flm.irai.ankohonana.model.Ankohonana;
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
public interface AnkohonanaRepository extends JpaRepository<Ankohonana, UUID> {

    // Recherche par faritra
    List<Ankohonana> findByFaritraId(UUID faritraId);

    // Recherche par faritra avec pagination
    Page<Ankohonana> findByFaritraId(UUID faritraId, Pageable pageable);

    // Recherche par nom du chef de famille (partiel, insensible a la casse)
    Page<Ankohonana> findByNomChefFamilleContainingIgnoreCase(String nomChefFamille, Pageable pageable);

    // Recherche par reference livre
    Optional<Ankohonana> findByReferenceLivre(String referenceLivre);

    // Verification d'existence par reference livre dans un faritra
    boolean existsByReferenceLivreAndFaritraId(String referenceLivre, UUID faritraId);

    // Verification d'existence par reference livre dans une fiangonana (via faritra)
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Ankohonana a " +
           "WHERE a.referenceLivre = :referenceLivre AND a.faritra.fiangonana.id = :fiangonanaId")
    boolean existsByReferenceLivreAndFiangonanaId(
            @Param("referenceLivre") String referenceLivre,
            @Param("fiangonanaId") UUID fiangonanaId
    );

    // Verification d'existence par reference livre dans une fiangonana (excluant un id)
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Ankohonana a " +
           "WHERE a.referenceLivre = :referenceLivre AND a.faritra.fiangonana.id = :fiangonanaId AND a.id != :excludeId")
    boolean existsByReferenceLivreAndFiangonanaIdExcludingId(
            @Param("referenceLivre") String referenceLivre,
            @Param("fiangonanaId") UUID fiangonanaId,
            @Param("excludeId") UUID excludeId
    );

    // Compte les ankohonana d'un faritra
    long countByFaritraId(UUID faritraId);

    // Recherche avec filtres optionnels
    @Query("SELECT a FROM Ankohonana a WHERE " +
           "(:faritraId IS NULL OR a.faritra.id = :faritraId) AND " +
           "(COALESCE(:nomChefFamille, '') = '' OR LOWER(a.nomChefFamille) LIKE LOWER(CONCAT('%', CAST(:nomChefFamille AS string), '%'))) AND " +
           "(COALESCE(:referenceLivre, '') = '' OR LOWER(a.referenceLivre) LIKE LOWER(CONCAT('%', CAST(:referenceLivre AS string), '%')))")
    Page<Ankohonana> findWithFilters(
            @Param("faritraId") UUID faritraId,
            @Param("nomChefFamille") String nomChefFamille,
            @Param("referenceLivre") String referenceLivre,
            Pageable pageable
    );

    // Recherche des ankohonana d'une fiangonana (via faritra)
    @Query("SELECT a FROM Ankohonana a WHERE a.faritra.fiangonana.id = :fiangonanaId")
    List<Ankohonana> findByFiangonanaId(@Param("fiangonanaId") UUID fiangonanaId);

    // Recherche des ankohonana d'une fiangonana avec pagination
    @Query("SELECT a FROM Ankohonana a WHERE a.faritra.fiangonana.id = :fiangonanaId")
    Page<Ankohonana> findByFiangonanaId(@Param("fiangonanaId") UUID fiangonanaId, Pageable pageable);
}
