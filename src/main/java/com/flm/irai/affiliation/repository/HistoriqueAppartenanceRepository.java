package com.flm.irai.affiliation.repository;

import com.flm.irai.affiliation.model.HistoriqueAppartenance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HistoriqueAppartenanceRepository extends JpaRepository<HistoriqueAppartenance, UUID> {

    // Historique complet d'un Kristiana
    List<HistoriqueAppartenance> findByKristianaIdOrderByDateDebutDesc(UUID kristianaId);

    Page<HistoriqueAppartenance> findByKristianaId(UUID kristianaId, Pageable pageable);

    // Recherche l'affiliation active d'un Kristiana (dateFin = null)
    Optional<HistoriqueAppartenance> findByKristianaIdAndDateFinIsNull(UUID kristianaId);

    // Verifie si un Kristiana a une affiliation active
    boolean existsByKristianaIdAndDateFinIsNull(UUID kristianaId);

    // Historique par Ankohonana
    List<HistoriqueAppartenance> findByAnkohonanaId(UUID ankohonanaId);

    Page<HistoriqueAppartenance> findByAnkohonanaId(UUID ankohonanaId, Pageable pageable);

    // Membres actuels d'une Ankohonana
    List<HistoriqueAppartenance> findByAnkohonanaIdAndDateFinIsNull(UUID ankohonanaId);

    Page<HistoriqueAppartenance> findByAnkohonanaIdAndDateFinIsNull(UUID ankohonanaId, Pageable pageable);

    // Compte les membres actuels d'une Ankohonana
    long countByAnkohonanaIdAndDateFinIsNull(UUID ankohonanaId);
}
