package com.flm.irai.ankohonana.service;

import com.flm.irai.ankohonana.model.Ankohonana;
import com.flm.irai.ankohonana.repository.AnkohonanaRepository;
import com.flm.irai.common.exception.ResourceNotFoundException;
import com.flm.irai.faritra.model.Faritra;
import com.flm.irai.faritra.repository.FaritraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnkohonanaService {

    private final AnkohonanaRepository ankohonanaRepository;
    private final FaritraRepository faritraRepository;

    // Recupere une ankohonana par son ID
    public Ankohonana getById(UUID id) {
        return findAnkohonanaById(id);
    }

    // Liste paginee avec filtres optionnels
    public Page<Ankohonana> getAll(UUID faritraId, String nomChefFamille, String referenceLivre, Pageable pageable) {
        return ankohonanaRepository.findWithFilters(faritraId, nomChefFamille, referenceLivre, pageable);
    }

    // Liste des ankohonana d'un faritra
    public List<Ankohonana> getByFaritraId(UUID faritraId) {
        // Verification que le faritra existe
        findFaritraById(faritraId);
        return ankohonanaRepository.findByFaritraId(faritraId);
    }

    // Liste paginee des ankohonana d'un faritra
    public Page<Ankohonana> getByFaritraId(UUID faritraId, Pageable pageable) {
        // Verification que le faritra existe
        findFaritraById(faritraId);
        return ankohonanaRepository.findByFaritraId(faritraId, pageable);
    }

    // Creation d'une ankohonana
    @Transactional
    public Ankohonana create(String nomChefFamille, String referenceLivre, UUID faritraId) {
        // Recuperation et validation du faritra
        Faritra faritra = findFaritraById(faritraId);

        // Validation de l'unicite de referenceLivre dans la fiangonana (si fournie)
        if (referenceLivre != null && !referenceLivre.isBlank()) {
            UUID fiangonanaId = faritra.getFiangonana().getId();
            if (ankohonanaRepository.existsByReferenceLivreAndFiangonanaId(referenceLivre, fiangonanaId)) {
                throw new IllegalArgumentException(
                        "La reference livre '" + referenceLivre + "' existe deja dans cette fiangonana"
                );
            }
        }

        // Creation de l'entite
        Ankohonana ankohonana = Ankohonana.builder()
                .nomChefFamille(nomChefFamille)
                .referenceLivre(referenceLivre)
                .faritra(faritra)
                .build();

        return ankohonanaRepository.save(ankohonana);
    }

    // Modification d'une ankohonana
    @Transactional
    public Ankohonana update(UUID id, String nomChefFamille, String referenceLivre, UUID faritraId) {
        Ankohonana ankohonana = findAnkohonanaById(id);

        // Mise a jour du nom du chef de famille si fourni
        if (nomChefFamille != null) {
            ankohonana.setNomChefFamille(nomChefFamille);
        }

        // Mise a jour de la reference livre si fournie
        if (referenceLivre != null && !referenceLivre.equals(ankohonana.getReferenceLivre())) {
            // Determiner la fiangonana cible
            UUID targetFiangonanaId;
            if (faritraId != null) {
                Faritra newFaritra = findFaritraById(faritraId);
                targetFiangonanaId = newFaritra.getFiangonana().getId();
            } else {
                targetFiangonanaId = ankohonana.getFaritra().getFiangonana().getId();
            }

            // Verification de l'unicite (en excluant l'ankohonana actuelle)
            if (!referenceLivre.isBlank() && 
                ankohonanaRepository.existsByReferenceLivreAndFiangonanaIdExcludingId(referenceLivre, targetFiangonanaId, id)) {
                throw new IllegalArgumentException(
                        "La reference livre '" + referenceLivre + "' existe deja dans cette fiangonana"
                );
            }
            ankohonana.setReferenceLivre(referenceLivre);
        }

        // Mise a jour du faritra si fourni
        if (faritraId != null && !faritraId.equals(ankohonana.getFaritra().getId())) {
            Faritra newFaritra = findFaritraById(faritraId);
            ankohonana.setFaritra(newFaritra);
        }

        return ankohonanaRepository.save(ankohonana);
    }

    // Suppression logique (Soft Delete)
    @Transactional
    public void delete(UUID id) {
        Ankohonana ankohonana = findAnkohonanaById(id);
        ankohonana.softDelete();
        ankohonanaRepository.save(ankohonana);
    }

    // Methodes privees utilitaires

    private Ankohonana findAnkohonanaById(UUID id) {
        return ankohonanaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ankohonana", id));
    }

    private Faritra findFaritraById(UUID id) {
        return faritraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faritra", id));
    }
}
