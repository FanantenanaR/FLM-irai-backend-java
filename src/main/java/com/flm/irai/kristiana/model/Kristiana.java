package com.flm.irai.kristiana.model;

import com.flm.irai.common.entity.AbstractAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

// Entite representant un membre (Kristiana) de l'eglise
@Entity
@Table(name = "kristiana")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Kristiana extends AbstractAuditableEntity {

    @NotBlank(message = "Le matricule est obligatoire")
    @Size(max = 100, message = "Le matricule ne peut pas depasser 100 caracteres")
    @Column(name = "matricule", unique = true, nullable = false, length = 100)
    private String matricule;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 255, message = "Le nom ne peut pas depasser 255 caracteres")
    @Column(name = "nom", nullable = false)
    private String nom;

    @NotBlank(message = "Le prenom est obligatoire")
    @Size(max = 255, message = "Le prenom ne peut pas depasser 255 caracteres")
    @Column(name = "prenom", nullable = false)
    private String prenom;

    @Size(max = 10, message = "Le sexe ne peut pas depasser 10 caracteres")
    @Column(name = "sexe", length = 10)
    private String sexe;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Size(max = 255, message = "Le lieu de naissance ne peut pas depasser 255 caracteres")
    @Column(name = "lieu_naissance")
    private String lieuNaissance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pere_biologique_id")
    private Kristiana pereBiologique;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mere_biologique_id")
    private Kristiana mereBiologique;
}
