package com.flm.irai.affiliation.service;

import com.flm.irai.affiliation.model.HistoriqueAppartenance;
import com.flm.irai.affiliation.repository.HistoriqueAppartenanceRepository;
import com.flm.irai.ankohonana.model.Ankohonana;
import com.flm.irai.ankohonana.repository.AnkohonanaRepository;
import com.flm.irai.common.exception.ResourceNotFoundException;
import com.flm.irai.kristiana.model.Kristiana;
import com.flm.irai.kristiana.repository.KristianaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AffiliationService {

    private final HistoriqueAppartenanceRepository historiqueRepository;
    private final KristianaRepository kristianaRepository;
    private final AnkohonanaRepository ankohonanaRepository;

    // Historique complet d'un Kristiana
    public List<HistoriqueAppartenance> getHistorique(UUID kristianaId) {
        return historiqueRepository.findByKristianaIdOrderByDateDebutDesc(kristianaId);
    }

    // Historique pagine d'un Kristiana
    public Page<HistoriqueAppartenance> getHistoriquePagine(UUID kristianaId, Pageable pageable) {
        return historiqueRepository.findByKristianaId(kristianaId, pageable);
    }

    // Affiliation actuelle d'un Kristiana
    public Optional<HistoriqueAppartenance> getAffiliationActuelle(UUID kristianaId) {
        return historiqueRepository.findByKristianaIdAndDateFinIsNull(kristianaId);
    }

    // Membres actuels d'une Ankohonana
    public Page<HistoriqueAppartenance> getMembresActuels(UUID ankohonanaId, Pageable pageable) {
        return historiqueRepository.findByAnkohonanaIdAndDateFinIsNull(ankohonanaId, pageable);
    }

    // Transfert d'un Kristiana vers une nouvelle Ankohonana
    @Transactional
    public HistoriqueAppartenance transferer(UUID kristianaId, UUID targetAnkohonanaId, String motif, String roleInterne) {
        Kristiana kristiana = findKristianaById(kristianaId);
        Ankohonana nouvelleAnkohonana = findAnkohonanaById(targetAnkohonanaId);

        // Fermer l'affiliation active si elle existe
        Optional<HistoriqueAppartenance> affiliationActive = historiqueRepository.findByKristianaIdAndDateFinIsNull(kristianaId);
        if (affiliationActive.isPresent()) {
            HistoriqueAppartenance ancienne = affiliationActive.get();
            ancienne.terminer(motif != null ? motif : "Transfert");
            historiqueRepository.save(ancienne);
        }

        // Creer la nouvelle affiliation
        HistoriqueAppartenance nouvelle = HistoriqueAppartenance.builder()
                .kristiana(kristiana)
                .ankohonana(nouvelleAnkohonana)
                .roleInterne(roleInterne != null ? roleInterne : "Membre")
                .dateDebut(LocalDate.now())
                .build();

        return historiqueRepository.save(nouvelle);
    }

    // Premiere affiliation d'un Kristiana (sans transfert)
    @Transactional
    public HistoriqueAppartenance affilier(UUID kristianaId, UUID ankohonanaId, String roleInterne) {
        // Verifier qu'il n'y a pas deja une affiliation active
        if (historiqueRepository.existsByKristianaIdAndDateFinIsNull(kristianaId)) {
            throw new IllegalArgumentException("Ce Kristiana a deja une affiliation active. Utilisez le transfert.");
        }

        Kristiana kristiana = findKristianaById(kristianaId);
        Ankohonana ankohonana = findAnkohonanaById(ankohonanaId);

        HistoriqueAppartenance affiliation = HistoriqueAppartenance.builder()
                .kristiana(kristiana)
                .ankohonana(ankohonana)
                .roleInterne(roleInterne != null ? roleInterne : "Membre")
                .dateDebut(LocalDate.now())
                .build();

        return historiqueRepository.save(affiliation);
    }

    // Terminer une affiliation (depart sans transfert)
    @Transactional
    public HistoriqueAppartenance terminer(UUID kristianaId, String motif) {
        HistoriqueAppartenance affiliation = historiqueRepository.findByKristianaIdAndDateFinIsNull(kristianaId)
                .orElseThrow(() -> new IllegalArgumentException("Ce Kristiana n'a pas d'affiliation active"));

        affiliation.terminer(motif != null ? motif : "Depart");
        return historiqueRepository.save(affiliation);
    }

    // Methodes utilitaires
    private Kristiana findKristianaById(UUID id) {
        return kristianaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kristiana", id));
    }

    private Ankohonana findAnkohonanaById(UUID id) {
        return ankohonanaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ankohonana", id));
    }
}
