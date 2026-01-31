package com.flm.irai.sampana.service;

import com.flm.irai.common.exception.ResourceNotFoundException;
import com.flm.irai.kristiana.model.Kristiana;
import com.flm.irai.kristiana.repository.KristianaRepository;
import com.flm.irai.sampana.model.AppartenanceSampana;
import com.flm.irai.sampana.model.Sampana;
import com.flm.irai.sampana.repository.AppartenanceSampanaRepository;
import com.flm.irai.sampana.repository.SampanaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppartenanceSampanaService {

    private final AppartenanceSampanaRepository appartenanceRepository;
    private final SampanaRepository sampanaRepository;
    private final KristianaRepository kristianaRepository;

    // Recupere une appartenance par son ID
    public AppartenanceSampana getById(UUID id) {
        return findAppartenanceById(id);
    }

    // Liste les membres d'un Sampana (actifs)
    public Page<AppartenanceSampana> getMembres(UUID sampanaId, Pageable pageable) {
        return appartenanceRepository.findBySampanaIdAndEstActifTrue(sampanaId, pageable);
    }

    // Liste les Sampana d'un Kristiana (actifs)
    public List<AppartenanceSampana> getSampanasOfKristiana(UUID kristianaId) {
        return appartenanceRepository.findByKristianaIdAndEstActifTrue(kristianaId);
    }

    // Historique complet d'un Kristiana
    public Page<AppartenanceSampana> getHistorique(UUID kristianaId, Pageable pageable) {
        return appartenanceRepository.findByKristianaId(kristianaId, pageable);
    }

    // Inscrit un Kristiana a un Sampana
    @Transactional
    public AppartenanceSampana inscrire(UUID kristianaId, UUID sampanaId, String role) {
        // Verifier que le Kristiana n'est pas deja membre actif
        if (appartenanceRepository.existsByKristianaIdAndSampanaIdAndEstActifTrue(kristianaId, sampanaId)) {
            throw new IllegalArgumentException("Ce Kristiana est deja membre actif de ce Sampana");
        }

        Kristiana kristiana = findKristianaById(kristianaId);
        Sampana sampana = findSampanaById(sampanaId);

        AppartenanceSampana appartenance = AppartenanceSampana.builder()
                .kristiana(kristiana)
                .sampana(sampana)
                .roleDansSampana(role != null ? role : "MEMBRE")
                .dateDebut(LocalDate.now())
                .estActif(true)
                .build();

        return appartenanceRepository.save(appartenance);
    }

    // Modifie le role d'un membre
    @Transactional
    public AppartenanceSampana modifierRole(UUID appartenanceId, String nouveauRole) {
        AppartenanceSampana appartenance = findAppartenanceById(appartenanceId);

        if (!appartenance.isActive()) {
            throw new IllegalArgumentException("Impossible de modifier une appartenance terminee");
        }

        appartenance.setRoleDansSampana(nouveauRole);
        return appartenanceRepository.save(appartenance);
    }

    // Termine l'appartenance d'un Kristiana a un Sampana
    @Transactional
    public AppartenanceSampana desinscrire(UUID kristianaId, UUID sampanaId) {
        AppartenanceSampana appartenance = appartenanceRepository
                .findByKristianaIdAndSampanaIdAndEstActifTrue(kristianaId, sampanaId)
                .orElseThrow(() -> new IllegalArgumentException("Ce Kristiana n'est pas membre actif de ce Sampana"));

        appartenance.terminer(LocalDate.now());
        return appartenanceRepository.save(appartenance);
    }

    // Suppression logique
    @Transactional
    public void delete(UUID id) {
        AppartenanceSampana appartenance = findAppartenanceById(id);
        appartenance.softDelete();
        appartenanceRepository.save(appartenance);
    }

    // Methodes utilitaires

    private AppartenanceSampana findAppartenanceById(UUID id) {
        return appartenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AppartenanceSampana", id));
    }

    private Sampana findSampanaById(UUID id) {
        return sampanaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sampana", id));
    }

    private Kristiana findKristianaById(UUID id) {
        return kristianaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kristiana", id));
    }
}
