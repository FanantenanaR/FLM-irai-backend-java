package com.flm.irai.sampana.model;

import com.flm.irai.common.entity.AbstractAuditableEntity;
import com.flm.irai.kristiana.model.Kristiana;
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
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

// Table d'association entre Kristiana et Sampana (Many-to-Many avec attributs)
@Entity
@Table(name = "appartenance_sampana")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AppartenanceSampana extends AbstractAuditableEntity {

    @NotNull(message = "Le Kristiana est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kristiana_id", nullable = false)
    private Kristiana kristiana;

    @NotNull(message = "Le Sampana est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sampana_id", nullable = false)
    private Sampana sampana;

    @Size(max = 100, message = "Le role ne peut pas depasser 100 caracteres")
    @Column(name = "role_dans_sampana", length = 100)
    @Builder.Default
    private String roleDansSampana = "MEMBRE";

    @NotNull(message = "La date de debut est obligatoire")
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(name = "est_actif")
    @Builder.Default
    private boolean estActif = true;

    // Termine l'appartenance a une date donnee
    public void terminer(LocalDate dateFin) {
        this.dateFin = dateFin;
        this.estActif = false;
    }

    // Verifie si l'appartenance est active
    public boolean isActive() {
        return this.estActif && this.dateFin == null;
    }
}
