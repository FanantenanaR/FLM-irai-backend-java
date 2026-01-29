package com.flm.irai.utilisateur.service;

import com.flm.irai.common.exception.ResourceNotFoundException;
import com.flm.irai.utilisateur.model.Utilisateur;
import com.flm.irai.utilisateur.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    // Recuperation par ID
    public Utilisateur getById(UUID id) {
        return findUtilisateurById(id);
    }

    // Recuperation par email
    public Utilisateur getByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));
    }

    // Liste paginee avec filtres optionnels
    public Page<Utilisateur> getAll(Boolean estActif, String email, Pageable pageable) {
        return utilisateurRepository.findWithFilters(estActif, email, pageable);
    }

    // Creation d'un utilisateur
    @Transactional
    public Utilisateur create(String email, String motDePasseHash, UUID kristianaId) {
        // Validation : email unique
        if (utilisateurRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("L'email '" + email + "' est deja utilise");
        }

        // Validation : kristianaId unique (si fourni)
        if (kristianaId != null && utilisateurRepository.existsByKristianaId(kristianaId)) {
            throw new IllegalArgumentException("Ce kristiana possede deja un compte utilisateur");
        }

        Utilisateur utilisateur = Utilisateur.builder()
                .email(email)
                .motDePasseHash(motDePasseHash)
                .kristianaId(kristianaId)
                .estActif(true)
                .build();

        return utilisateurRepository.save(utilisateur);
    }

    // Modification d'un utilisateur
    @Transactional
    public Utilisateur update(UUID id, String email, UUID kristianaId, Boolean estActif) {
        Utilisateur utilisateur = findUtilisateurById(id);

        // Mise a jour de l'email si fourni
        if (email != null && !email.equals(utilisateur.getEmail())) {
            if (utilisateurRepository.existsByEmailExcludingId(email, id)) {
                throw new IllegalArgumentException("L'email '" + email + "' est deja utilise");
            }
            utilisateur.setEmail(email);
        }

        // Mise a jour du kristianaId si fourni
        if (kristianaId != null && !kristianaId.equals(utilisateur.getKristianaId())) {
            if (utilisateurRepository.existsByKristianaId(kristianaId)) {
                throw new IllegalArgumentException("Ce kristiana possede deja un compte utilisateur");
            }
            utilisateur.setKristianaId(kristianaId);
        }

        // Mise a jour du statut actif si fourni
        if (estActif != null) {
            utilisateur.setEstActif(estActif);
        }

        return utilisateurRepository.save(utilisateur);
    }

    // Modification du mot de passe
    @Transactional
    public Utilisateur updatePassword(UUID id, String nouveauMotDePasseHash) {
        Utilisateur utilisateur = findUtilisateurById(id);
        utilisateur.setMotDePasseHash(nouveauMotDePasseHash);
        return utilisateurRepository.save(utilisateur);
    }

    // Activation / Desactivation d'un utilisateur
    @Transactional
    public Utilisateur toggleActive(UUID id) {
        Utilisateur utilisateur = findUtilisateurById(id);
        utilisateur.setEstActif(!utilisateur.isEstActif());
        return utilisateurRepository.save(utilisateur);
    }

    // Suppression (Soft Delete)
    @Transactional
    public void delete(UUID id) {
        Utilisateur utilisateur = findUtilisateurById(id);
        utilisateur.softDelete();
        utilisateurRepository.save(utilisateur);
    }

    // Verification si un email existe
    public boolean emailExists(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

    // Methode utilitaire : recherche par ID avec exception
    private Utilisateur findUtilisateurById(UUID id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", id));
    }
}
