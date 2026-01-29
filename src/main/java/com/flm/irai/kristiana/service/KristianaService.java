package com.flm.irai.kristiana.service;

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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KristianaService {

    private final KristianaRepository kristianaRepository;

    // Recuperation par ID
    public Kristiana getById(UUID id) {
        return findKristianaById(id);
    }

    // Recuperation par matricule
    public Kristiana getByMatricule(String matricule) {
        return kristianaRepository.findByMatricule(matricule)
                .orElseThrow(() -> new ResourceNotFoundException("Kristiana", "matricule", matricule));
    }

    // Liste paginee avec filtres optionnels
    public Page<Kristiana> getAll(String nom, String prenom, String matricule, String sexe, Pageable pageable) {
        return kristianaRepository.findWithFilters(nom, prenom, matricule, sexe, pageable);
    }

    // Recherche globale
    public Page<Kristiana> search(String search, Pageable pageable) {
        return kristianaRepository.searchGlobal(search, pageable);
    }

    // Recuperation des enfants d'un parent
    public List<Kristiana> getEnfants(UUID parentId) {
        List<Kristiana> enfantsPere = kristianaRepository.findByPereBiologiqueId(parentId);
        List<Kristiana> enfantsMere = kristianaRepository.findByMereBiologiqueId(parentId);
        
        // Fusion des deux listes sans doublons
        enfantsPere.addAll(enfantsMere.stream()
                .filter(e -> !enfantsPere.contains(e))
                .toList());
        
        return enfantsPere;
    }

    // Creation d'un kristiana
    @Transactional
    public Kristiana create(String matricule, String nom, String prenom, String sexe,
                            LocalDate dateNaissance, String lieuNaissance,
                            UUID pereBiologiqueId, UUID mereBiologiqueId) {
        // Validation : matricule unique
        if (kristianaRepository.existsByMatricule(matricule)) {
            throw new IllegalArgumentException("Le matricule '" + matricule + "' existe deja");
        }

        // Validation : sexe
        if (sexe != null && !isValidSexe(sexe)) {
            throw new IllegalArgumentException("Le sexe doit etre 'M' ou 'F'");
        }

        // Construction de l'entite
        Kristiana kristiana = Kristiana.builder()
                .matricule(matricule)
                .nom(nom)
                .prenom(prenom)
                .sexe(sexe)
                .dateNaissance(dateNaissance)
                .lieuNaissance(lieuNaissance)
                .build();

        // Association du pere biologique si fourni
        if (pereBiologiqueId != null) {
            Kristiana pere = findKristianaById(pereBiologiqueId);
            kristiana.setPereBiologique(pere);
        }

        // Association de la mere biologique si fournie
        if (mereBiologiqueId != null) {
            Kristiana mere = findKristianaById(mereBiologiqueId);
            kristiana.setMereBiologique(mere);
        }

        return kristianaRepository.save(kristiana);
    }

    // Modification d'un kristiana
    @Transactional
    public Kristiana update(UUID id, String matricule, String nom, String prenom, String sexe,
                            LocalDate dateNaissance, String lieuNaissance,
                            UUID pereBiologiqueId, UUID mereBiologiqueId) {
        Kristiana kristiana = findKristianaById(id);

        // Mise a jour du matricule si fourni
        if (matricule != null && !matricule.equals(kristiana.getMatricule())) {
            if (kristianaRepository.existsByMatriculeExcludingId(matricule, id)) {
                throw new IllegalArgumentException("Le matricule '" + matricule + "' existe deja");
            }
            kristiana.setMatricule(matricule);
        }

        // Mise a jour des champs simples
        if (nom != null) {
            kristiana.setNom(nom);
        }
        if (prenom != null) {
            kristiana.setPrenom(prenom);
        }
        if (sexe != null) {
            if (!isValidSexe(sexe)) {
                throw new IllegalArgumentException("Le sexe doit etre 'M' ou 'F'");
            }
            kristiana.setSexe(sexe);
        }
        if (dateNaissance != null) {
            kristiana.setDateNaissance(dateNaissance);
        }
        if (lieuNaissance != null) {
            kristiana.setLieuNaissance(lieuNaissance);
        }

        // Mise a jour du pere biologique
        if (pereBiologiqueId != null) {
            if (pereBiologiqueId.equals(id)) {
                throw new IllegalArgumentException("Un kristiana ne peut pas etre son propre pere");
            }
            Kristiana pere = findKristianaById(pereBiologiqueId);
            kristiana.setPereBiologique(pere);
        }

        // Mise a jour de la mere biologique
        if (mereBiologiqueId != null) {
            if (mereBiologiqueId.equals(id)) {
                throw new IllegalArgumentException("Un kristiana ne peut pas etre sa propre mere");
            }
            Kristiana mere = findKristianaById(mereBiologiqueId);
            kristiana.setMereBiologique(mere);
        }

        return kristianaRepository.save(kristiana);
    }

    // Suppression (Soft Delete)
    @Transactional
    public void delete(UUID id) {
        Kristiana kristiana = findKristianaById(id);
        kristiana.softDelete();
        kristianaRepository.save(kristiana);
    }

    // Verification si un matricule existe
    public boolean matriculeExists(String matricule) {
        return kristianaRepository.existsByMatricule(matricule);
    }

    // Methode utilitaire : recherche par ID avec exception
    private Kristiana findKristianaById(UUID id) {
        return kristianaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kristiana", id));
    }

    // Validation du sexe
    private boolean isValidSexe(String sexe) {
        return "M".equalsIgnoreCase(sexe) || "F".equalsIgnoreCase(sexe);
    }
}
