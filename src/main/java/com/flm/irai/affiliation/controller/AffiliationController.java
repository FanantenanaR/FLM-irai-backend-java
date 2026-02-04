package com.flm.irai.affiliation.controller;

import com.flm.irai.affiliation.dto.AffiliationRequest;
import com.flm.irai.affiliation.dto.TransfertRequest;
import com.flm.irai.affiliation.model.HistoriqueAppartenance;
import com.flm.irai.affiliation.service.AffiliationService;
import com.flm.irai.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/affiliations")
@RequiredArgsConstructor
public class AffiliationController {

    private final AffiliationService affiliationService;

    // POST /api/v1/affiliations - Premiere affiliation
    @PostMapping
    public ResponseEntity<ApiResponse<HistoriqueAppartenance>> affilier(
            @Valid @RequestBody AffiliationRequest request
    ) {
        HistoriqueAppartenance affiliation = affiliationService.affilier(
                request.kristianaId(),
                request.ankohonanaId(),
                request.roleInterne()
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Affiliation creee avec succes", affiliation));
    }

    // POST /api/v1/affiliations/transfert - Transfert vers une nouvelle Ankohonana
    @PostMapping("/transfert")
    public ResponseEntity<ApiResponse<HistoriqueAppartenance>> transferer(
            @Valid @RequestBody TransfertRequest request
    ) {
        HistoriqueAppartenance affiliation = affiliationService.transferer(
                request.kristianaId(),
                request.targetAnkohonanaId(),
                request.motif(),
                request.roleInterne()
        );
        return ResponseEntity.ok(
                ApiResponse.success("Transfert effectue avec succes", affiliation)
        );
    }
}
