package com.flm.irai.faritra.model;

import com.flm.irai.common.entity.AbstractAuditableEntity;
import com.flm.irai.organisation.model.Organisation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

// Entite representant un Faritra (secteur/quartier) d'une Fiangonana
@Entity
@Table(name = "faritra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Faritra extends AbstractAuditableEntity {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 255, message = "Le nom ne peut pas depasser 255 caracteres")
    @Column(name = "nom", nullable = false)
    private String nom;

    @NotNull(message = "La fiangonana est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fiangonana_id", nullable = false)
    private Organisation fiangonana;
}
