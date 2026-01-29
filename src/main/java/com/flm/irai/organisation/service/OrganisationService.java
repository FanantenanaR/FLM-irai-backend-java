package com.flm.irai.organisation.service;

import com.flm.irai.common.exception.ResourceNotFoundException;
import com.flm.irai.organisation.model.Organisation;
import com.flm.irai.organisation.model.TypeOrganisation;
import com.flm.irai.organisation.repository.OrganisationRepository;
import com.flm.irai.organisation.repository.TypeOrganisationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganisationService {

    private final OrganisationRepository organisationRepository;
    private final TypeOrganisationRepository typeOrganisationRepository;

    // Recupere une organisation par son ID
    public Organisation getById(UUID id) {
        return findOrganisationById(id);
    }

    // Liste paginee avec filtres optionnels
    public Page<Organisation> getAll(UUID typeId, UUID parentId, Pageable pageable) {
        return organisationRepository.findWithFilters(typeId, parentId, pageable);
    }

    // Creation d'une organisation
    @Transactional
    public Organisation create(String code, String nom, UUID typeId, UUID parentId) {
        // Verification unicite du code
        if (organisationRepository.existsByCode(code)) {
            throw new IllegalArgumentException("Le code '" + code + "' existe deja");
        }

        // Recuperation du type
        TypeOrganisation type = findTypeById(typeId);

        // Recuperation du parent (optionnel)
        Organisation parent = null;
        if (parentId != null) {
            parent = findOrganisationById(parentId);
            validateHierarchy(type, parent.getType());
        } else {
            // Seul FOIBE peut ne pas avoir de parent
            if (!"FOIBE".equals(type.getCode())) {
                throw new IllegalArgumentException("Seul le FOIBE peut ne pas avoir de parent");
            }
        }

        // Construction du chemin hierarchique
        String cheminHierarchique = buildCheminHierarchique(code, parent);

        // Creation de l'entite
        Organisation organisation = Organisation.builder()
                .code(code)
                .nom(nom)
                .type(type)
                .parent(parent)
                .cheminHierarchique(cheminHierarchique)
                .build();

        return organisationRepository.save(organisation);
    }

    // Modification d'une organisation
    @Transactional
    public Organisation update(UUID id, String code, String nom, UUID typeId, UUID parentId) {
        Organisation organisation = findOrganisationById(id);

        // Mise a jour du code si fourni
        if (code != null && !code.equals(organisation.getCode())) {
            if (organisationRepository.existsByCode(code)) {
                throw new IllegalArgumentException("Le code '" + code + "' existe deja");
            }
            organisation.setCode(code);
        }

        // Mise a jour du nom si fourni
        if (nom != null) {
            organisation.setNom(nom);
        }

        // Mise a jour du type si fourni
        if (typeId != null) {
            TypeOrganisation newType = findTypeById(typeId);
            if (organisation.getParent() != null) {
                validateHierarchy(newType, organisation.getParent().getType());
            } else {
                // Sans parent, seul FOIBE est autorise
                if (!"FOIBE".equals(newType.getCode())) {
                    throw new IllegalArgumentException("Seul le FOIBE peut ne pas avoir de parent");
                }
            }
            organisation.setType(newType);
        }

        // Mise a jour du parent si fourni
        if (parentId != null) {
            Organisation newParent = findOrganisationById(parentId);
            validateHierarchy(organisation.getType(), newParent.getType());
            organisation.setParent(newParent);
            organisation.setCheminHierarchique(buildCheminHierarchique(organisation.getCode(), newParent));
        }

        return organisationRepository.save(organisation);
    }

    // Suppression logique (Soft Delete)
    @Transactional
    public void delete(UUID id) {
        Organisation organisation = findOrganisationById(id);

        // Verification qu'il n'y a pas d'enfants actifs
        long enfantsCount = organisationRepository.countByParentId(id);
        if (enfantsCount > 0) {
            throw new IllegalArgumentException("Impossible de supprimer : " + enfantsCount + " organisation(s) enfant(s) existe(nt)");
        }

        organisation.softDelete();
        organisationRepository.save(organisation);
    }

    // Methodes privees utilitaires

    private Organisation findOrganisationById(UUID id) {
        return organisationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation", id));
    }

    private TypeOrganisation findTypeById(UUID id) {
        return typeOrganisationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TypeOrganisation", id));
    }

    // Validation de la coherence hierarchique
    private void validateHierarchy(TypeOrganisation childType, TypeOrganisation parentType) {
        int childLevel = getHierarchyLevel(childType.getCode());
        int parentLevel = getHierarchyLevel(parentType.getCode());

        if (childLevel <= parentLevel) {
            throw new IllegalArgumentException(
                    "Hierarchie invalide : un " + childType.getLibelle() +
                    " ne peut pas avoir un " + parentType.getLibelle() + " comme parent"
            );
        }

        if (childLevel != parentLevel + 1) {
            throw new IllegalArgumentException(
                    "Hierarchie invalide : un " + childType.getLibelle() +
                    " doit avoir un parent de niveau immediatement superieur"
            );
        }
    }

    // Retourne le niveau hierarchique (0 = FOIBE, 4 = FIANGONANA)
    private int getHierarchyLevel(String typeCode) {
        return switch (typeCode) {
            case "FOIBE" -> 0;
            case "SYNODA" -> 1;
            case "FILEOVANA" -> 2;
            case "FITANDREMANA" -> 3;
            case "FIANGONANA" -> 4;
            default -> throw new IllegalArgumentException("Type d'organisation inconnu : " + typeCode);
        };
    }

    // Construction du chemin hierarchique
    private String buildCheminHierarchique(String code, Organisation parent) {
        if (parent == null) {
            return code;
        }
        return parent.getCheminHierarchique() + "." + code;
    }
}
