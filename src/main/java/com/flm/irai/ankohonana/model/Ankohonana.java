package com.flm.irai.ankohonana.model;

import com.flm.irai.common.entity.AbstractAuditableEntity;
import com.flm.irai.faritra.model.Faritra;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

// Entite representant une Ankohonana (famille/foyer) rattachee a un Faritra
@Entity
@Table(name = "ankohonana")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Ankohonana extends AbstractAuditableEntity {

    @Size(max = 255, message = "Le nom du chef de famille ne peut pas depasser 255 caracteres")
    @Column(name = "nom_chef_famille")
    private String nomChefFamille;

    @Size(max = 255, message = "La reference livre ne peut pas depasser 255 caracteres")
    @Column(name = "reference_livre")
    private String referenceLivre;

    @NotNull(message = "Le faritra est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faritra_id", nullable = false)
    private Faritra faritra;
}
