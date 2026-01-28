package com.flm.irai.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Column(name = "cree_par")
    private UUID creePar;

    @LastModifiedDate
    @Column(name = "date_modification")
    private Instant dateModification;

    @Column(name = "modifie_par")
    private UUID modifiePar;

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
