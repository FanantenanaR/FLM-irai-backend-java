package com.flm.irai.kristiana.controller;

import com.flm.irai.affiliation.model.HistoriqueAppartenance;
import com.flm.irai.affiliation.service.AffiliationService;
import com.flm.irai.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/kristianas")
@RequiredArgsConstructor
public class KristianaController {

    private final AffiliationService affiliationService;

    // GET /api/v1/kristianas/{id}/historique-appartenance
    @GetMapping("/{id}/historique-appartenance")
    public ResponseEntity<ApiResponse<List<HistoriqueAppartenance>>> getHistorique(
            @PathVariable UUID id,
            @PageableDefault(size = 20, sort = "dateDebut", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<HistoriqueAppartenance> page = affiliationService.getHistoriquePagine(id, pageable);
        return ResponseEntity.ok(
                ApiResponse.successPaginated(
                        "Historique d'appartenance recupere avec succes",
                        page.getContent(),
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements()
                )
        );
    }
}
