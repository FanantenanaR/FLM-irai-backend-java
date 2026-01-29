package com.flm.irai.utilisateur.model;

import com.flm.irai.common.entity.AbstractAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

// Entite representant un utilisateur du systeme
@Entity
@Table(name = "utilisateur")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Utilisateur extends AbstractAuditableEntity {

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit etre valide")
    @Size(max = 255, message = "L'email ne peut pas depasser 255 caracteres")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(max = 255, message = "Le mot de passe hash ne peut pas depasser 255 caracteres")
    @Column(name = "mot_de_passe_hash", nullable = false)
    private String motDePasseHash;

    @Column(name = "kristiana_id")
    private UUID kristianaId;

    @Builder.Default
    @Column(name = "est_actif")
    private Boolean estActif = true;
}
