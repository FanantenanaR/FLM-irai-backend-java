package com.flm.irai.common.entity;

import com.flm.irai.utilisateur.model.Utilisateur;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

// Classe de base pour toutes les entites avec audit et soft delete
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("date_suppression IS NULL")
public abstract class AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreatedDate
    @Column(name = "date_creation", nullable = false, updatable = false)
    private Instant dateCreation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cree_par")
    private Utilisateur creePar;

    @LastModifiedDate
    @Column(name = "date_modification")
    private Instant dateModification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modifie_par")
    private Utilisateur modifiePar;

    @Column(name = "date_suppression")
    private Instant dateSuppression;

    // Soft delete : marque l'entite comme supprimee
    public void softDelete() {
        this.dateSuppression = Instant.now();
    }

    // Restaure une entite supprimee
    public void restore() {
        this.dateSuppression = null;
    }

    // Verifie si l'entite est supprimee
    public boolean isDeleted() {
        return this.dateSuppression != null;
    }
}
