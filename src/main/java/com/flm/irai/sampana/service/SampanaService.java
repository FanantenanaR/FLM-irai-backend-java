package com.flm.irai.sampana.service;

import com.flm.irai.common.exception.ResourceNotFoundException;
import com.flm.irai.organisation.model.Organisation;
import com.flm.irai.organisation.repository.OrganisationRepository;
import com.flm.irai.sampana.model.Sampana;
import com.flm.irai.sampana.model.TypeSampana;
import com.flm.irai.sampana.repository.SampanaRepository;
import com.flm.irai.sampana.repository.TypeSampanaRepository;
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
public class SampanaService {

    private final SampanaRepository sampanaRepository;
    private final TypeSampanaRepository typeSampanaRepository;
    private final OrganisationRepository organisationRepository;

    // Recupere un Sampana par son ID
    public Sampana getById(UUID id) {
        return findSampanaById(id);
    }

    // Liste paginee avec filtres optionnels
    public Page<Sampana> getAll(UUID fiangonanaId, UUID typeId, Pageable pageable) {
        return sampanaRepository.findWithFilters(fiangonanaId, typeId, pageable);
    }

    // Liste des Sampana d'une Fiangonana
    public List<Sampana> getByFiangonana(UUID fiangonanaId) {
        return sampanaRepository.findByFiangonanaId(fiangonanaId);
    }

    // Creation d'un Sampana
    @Transactional
    public Sampana create(String nom, UUID typeId, UUID fiangonanaId, String description) {
        // Recuperation du type
        TypeSampana type = findTypeById(typeId);

        // Recuperation et validation de la Fiangonana
        Organisation fiangonana = findFiangonanaById(fiangonanaId);
        validateFiangonana(fiangonana);

        // Creation de l'entite
        Sampana sampana = Sampana.builder()
                .nom(nom)
                .type(type)
                .fiangonana(fiangonana)
                .description(description)
                .build();

        return sampanaRepository.save(sampana);
    }

    // Modification d'un Sampana
    @Transactional
    public Sampana update(UUID id, String nom, UUID typeId, String description) {
        Sampana sampana = findSampanaById(id);

        if (nom != null) {
            sampana.setNom(nom);
        }

        if (typeId != null) {
            TypeSampana type = findTypeById(typeId);
            sampana.setType(type);
        }

        if (description != null) {
            sampana.setDescription(description);
        }

        return sampanaRepository.save(sampana);
    }

    // Suppression logique (Soft Delete)
    @Transactional
    public void delete(UUID id) {
        Sampana sampana = findSampanaById(id);
        sampana.softDelete();
        sampanaRepository.save(sampana);
    }

    // Methodes utilitaires

    private Sampana findSampanaById(UUID id) {
        return sampanaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sampana", id));
    }

    private TypeSampana findTypeById(UUID id) {
        return typeSampanaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TypeSampana", id));
    }

    private Organisation findFiangonanaById(UUID id) {
        return organisationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation", id));
    }

    // Verifie que l'organisation est bien une Fiangonana
    private void validateFiangonana(Organisation organisation) {
        if (!"FIANGONANA".equals(organisation.getType().getCode())) {
            throw new IllegalArgumentException(
                    "Un Sampana doit etre rattache a une Fiangonana, pas a un " + organisation.getType().getLibelle());
        }
    }
}
