package com.flm.irai.affiliation.dto;

import java.util.UUID;

// DTO pour la premiere affiliation d'un Kristiana
public record AffiliationRequest(
        UUID kristianaId,
        UUID ankohonanaId,
        String roleInterne
) {}
