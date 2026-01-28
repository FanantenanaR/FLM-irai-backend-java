package com.flm.irai.organisation.model;

import com.flm.irai.common.entity.AbstractAuditableEntity;
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



// Entite representant une organisation dans la hierarchie FLM (Foibe > Synoda > Fileovana > Fitandremana > Fiangonana)
@Entity
@Table(name = "organisation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Organisation extends AbstractAuditableEntity {

    @NotBlank(message = "Le code est obligatoire")
    @Size(max = 100, message = "Le code ne peut pas depasser 100 caracteres")
    @Column(name = "code", unique = true, nullable = false, length = 100)
    private String code;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 255, message = "Le nom ne peut pas depasser 255 caracteres")
    @Column(name = "nom", nullable = false)
    private String nom;

    @NotNull(message = "Le type est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private TypeOrganisation type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Organisation parent;

    @Column(name = "chemin_hierarchique", columnDefinition = "ltree")
    private String cheminHierarchique;
}
