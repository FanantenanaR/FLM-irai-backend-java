package com.flm.irai.ankohonana.controller;

import com.flm.irai.ankohonana.model.Ankohonana;
import com.flm.irai.ankohonana.service.AnkohonanaService;
import com.flm.irai.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ankohonanas")
@RequiredArgsConstructor
public class AnkohonanaController {

    private final AnkohonanaService ankohonanaService;

    // GET /api/v1/ankohonanas/{id} - Recuperer une ankohonana par ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Ankohonana>> getById(@PathVariable UUID id) {
        Ankohonana ankohonana = ankohonanaService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Ankohonana recuperee avec succes", ankohonana)
        );
    }

    // GET /api/v1/ankohonanas - Liste paginee avec filtres optionnels
    @GetMapping
    public ResponseEntity<ApiResponse<List<Ankohonana>>> getAll(
            @RequestParam(required = false) UUID faritraId,
            @RequestParam(required = false) String nomChefFamille,
            @RequestParam(required = false) String referenceLivre,
            @PageableDefault(size = 20, sort = "nomChefFamille", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<Ankohonana> page = ankohonanaService.getAll(faritraId, nomChefFamille, referenceLivre, pageable);
        return ResponseEntity.ok(
                ApiResponse.successPaginated(
                        "Liste des ankohonana recuperee avec succes",
                        page.getContent(),
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements()
                )
        );
    }

    // POST /api/v1/ankohonanas - Creer une ankohonana
    @PostMapping
    public ResponseEntity<ApiResponse<Ankohonana>> create(@Valid @RequestBody Ankohonana request) {
        Ankohonana ankohonana = ankohonanaService.create(
                request.getNomChefFamille(),
                request.getReferenceLivre(),
                request.getFaritra() != null ? request.getFaritra().getId() : null
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Ankohonana creee avec succes", ankohonana));
    }

    // PUT /api/v1/ankohonanas/{id} - Modifier une ankohonana
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Ankohonana>> update(
            @PathVariable UUID id,
            @RequestBody Ankohonana request
    ) {
        Ankohonana ankohonana = ankohonanaService.update(
                id,
                request.getNomChefFamille(),
                request.getReferenceLivre(),
                request.getFaritra() != null ? request.getFaritra().getId() : null
        );
        return ResponseEntity.ok(
                ApiResponse.success("Ankohonana modifiee avec succes", ankohonana)
        );
    }

    // DELETE /api/v1/ankohonanas/{id} - Supprimer une ankohonana (Soft Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        ankohonanaService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Ankohonana supprimee avec succes")
        );
    }
}
