package com.flm.irai.organisation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

// Table de reference pour les types d'organisation (FOIBE, SYNODA, FILEOVANA, FITANDREMANA, FIANGONANA)
@Entity
@Table(name = "type_organisation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypeOrganisation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Le code est obligatoire")
    @Size(max = 50, message = "Le code ne peut pas depasser 50 caracteres")
    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @NotBlank(message = "Le libelle est obligatoire")
    @Size(max = 255, message = "Le libelle ne peut pas depasser 255 caracteres")
    @Column(name = "libelle", nullable = false)
    private String libelle;
}
