package com.flm.irai.faritra.service;

import com.flm.irai.common.exception.ResourceNotFoundException;
import com.flm.irai.faritra.model.Faritra;
import com.flm.irai.faritra.repository.FaritraRepository;
import com.flm.irai.organisation.model.Organisation;
import com.flm.irai.organisation.repository.OrganisationRepository;
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
public class FaritraService {

    private static final String TYPE_FIANGONANA = "FIANGONANA";

    private final FaritraRepository faritraRepository;
    private final OrganisationRepository organisationRepository;

    // Recupere un faritra par son ID
    public Faritra getById(UUID id) {
        return findFaritraById(id);
    }

    // Liste paginee avec filtres optionnels
    public Page<Faritra> getAll(UUID fiangonanaId, String nom, Pageable pageable) {
        return faritraRepository.findWithFilters(fiangonanaId, nom, pageable);
    }

    // Liste des faritra d'une fiangonana
    public List<Faritra> getByFiangonanaId(UUID fiangonanaId) {
        // Verification que la fiangonana existe et est de type FIANGONANA
        Organisation fiangonana = findOrganisationById(fiangonanaId);
        validateFiangonanaType(fiangonana);

        return faritraRepository.findByFiangonanaId(fiangonanaId);
    }

    // Liste paginee des faritra d'une fiangonana
    public Page<Faritra> getByFiangonanaId(UUID fiangonanaId, Pageable pageable) {
        // Verification que la fiangonana existe et est de type FIANGONANA
        Organisation fiangonana = findOrganisationById(fiangonanaId);
        validateFiangonanaType(fiangonana);

        return faritraRepository.findByFiangonanaId(fiangonanaId, pageable);
    }

    // Creation d'un faritra
    @Transactional
    public Faritra create(String nom, UUID fiangonanaId) {
        // Recuperation et validation de la fiangonana
        Organisation fiangonana = findOrganisationById(fiangonanaId);
        validateFiangonanaType(fiangonana);

        // Verification unicite du nom dans la fiangonana (optionnel mais conseille)
        if (faritraRepository.existsByNomAndFiangonanaId(nom, fiangonanaId)) {
            throw new IllegalArgumentException(
                    "Un faritra avec le nom '" + nom + "' existe deja dans cette fiangonana"
            );
        }

        // Creation de l'entite
        Faritra faritra = Faritra.builder()
                .nom(nom)
                .fiangonana(fiangonana)
                .build();

        return faritraRepository.save(faritra);
    }

    // Modification d'un faritra
    @Transactional
    public Faritra update(UUID id, String nom, UUID fiangonanaId) {
        Faritra faritra = findFaritraById(id);

        // Mise a jour du nom si fourni
        if (nom != null && !nom.equals(faritra.getNom())) {
            UUID targetFiangonanaId = fiangonanaId != null ? fiangonanaId : faritra.getFiangonana().getId();
            if (faritraRepository.existsByNomAndFiangonanaId(nom, targetFiangonanaId)) {
                throw new IllegalArgumentException(
                        "Un faritra avec le nom '" + nom + "' existe deja dans cette fiangonana"
                );
            }
            faritra.setNom(nom);
        }

        // Mise a jour de la fiangonana si fournie
        if (fiangonanaId != null && !fiangonanaId.equals(faritra.getFiangonana().getId())) {
            Organisation newFiangonana = findOrganisationById(fiangonanaId);
            validateFiangonanaType(newFiangonana);
            faritra.setFiangonana(newFiangonana);
        }

        return faritraRepository.save(faritra);
    }

    // Suppression logique (Soft Delete)
    @Transactional
    public void delete(UUID id) {
        Faritra faritra = findFaritraById(id);
        faritra.softDelete();
        faritraRepository.save(faritra);
    }

    // Methodes privees utilitaires

    private Faritra findFaritraById(UUID id) {
        return faritraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faritra", id));
    }

    private Organisation findOrganisationById(UUID id) {
        return organisationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation", id));
    }

    // Validation que l'organisation est bien de type FIANGONANA
    private void validateFiangonanaType(Organisation organisation) {
        if (organisation.getType() == null || !TYPE_FIANGONANA.equals(organisation.getType().getCode())) {
            throw new IllegalArgumentException(
                    "L'organisation doit etre de type FIANGONANA. Type actuel : " +
                    (organisation.getType() != null ? organisation.getType().getCode() : "null")
            );
        }
    }
}
