package com.flm.irai.affiliation.dto;

import java.util.UUID;

// DTO pour le transfert d'un Kristiana vers une nouvelle Ankohonana
public record TransfertRequest(
        UUID kristianaId,
        UUID targetAnkohonanaId,
        String motif,
        String roleInterne
) {}
