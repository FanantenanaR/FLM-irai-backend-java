package com.flm.irai.affiliation.model;

import com.flm.irai.ankohonana.model.Ankohonana;
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
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

// Historique des appartenances d'un Kristiana a une Ankohonana
@Entity
@Table(name = "historique_appartenance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class HistoriqueAppartenance extends AbstractAuditableEntity {

    @NotNull(message = "Le kristiana est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kristiana_id", nullable = false)
    private Kristiana kristiana;

    @NotNull(message = "L'ankohonana est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ankohonana_id", nullable = false)
    private Ankohonana ankohonana;

    @Size(max = 100, message = "Le role ne peut pas depasser 100 caracteres")
    @Column(name = "role_interne", length = 100)
    private String roleInterne;

    @NotNull(message = "La date de debut est obligatoire")
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Size(max = 255, message = "Le motif ne peut pas depasser 255 caracteres")
    @Column(name = "motif")
    private String motif;

    // Verifie si l'appartenance est active
    public boolean isActive() {
        return this.dateFin == null;
    }

    // Termine l'appartenance
    public void terminer(String motif) {
        this.dateFin = LocalDate.now();
        this.motif = motif;
    }
}
