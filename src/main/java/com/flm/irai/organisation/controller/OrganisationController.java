package com.flm.irai.organisation.controller;

import com.flm.irai.common.dto.ApiResponse;
import com.flm.irai.organisation.model.Organisation;
import com.flm.irai.organisation.service.OrganisationService;
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
@RequestMapping("/api/v1/organisations")
@RequiredArgsConstructor
public class OrganisationController {

    private final OrganisationService organisationService;

    // GET /api/v1/organisations/{id} - Recuperer une organisation par ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Organisation>> getById(@PathVariable UUID id) {
        Organisation organisation = organisationService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Organisation recuperee avec succes", organisation)
        );
    }

    // GET /api/v1/organisations - Liste paginee avec filtres optionnels
    @GetMapping
    public ResponseEntity<ApiResponse<List<Organisation>>> getAll(
            @RequestParam(required = false) UUID typeId,
            @RequestParam(required = false) UUID parentId,
            @PageableDefault(size = 20, sort = "nom", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<Organisation> page = organisationService.getAll(typeId, parentId, pageable);
        return ResponseEntity.ok(
                ApiResponse.successPaginated(
                        "Liste des organisations recuperee avec succes",
                        page.getContent(),
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements()
                )
        );
    }

    // POST /api/v1/organisations - Creer une organisation
    @PostMapping
    public ResponseEntity<ApiResponse<Organisation>> create(@Valid @RequestBody Organisation request) {
        Organisation organisation = organisationService.create(
                request.getCode(),
                request.getNom(),
                request.getType() != null ? request.getType().getId() : null,
                request.getParent() != null ? request.getParent().getId() : null
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Organisation creee avec succes", organisation));
    }

    // PUT /api/v1/organisations/{id} - Modifier une organisation
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Organisation>> update(
            @PathVariable UUID id,
            @RequestBody Organisation request
    ) {
        Organisation organisation = organisationService.update(
                id,
                request.getCode(),
                request.getNom(),
                request.getType() != null ? request.getType().getId() : null,
                request.getParent() != null ? request.getParent().getId() : null
        );
        return ResponseEntity.ok(
                ApiResponse.success("Organisation modifiee avec succes", organisation)
        );
    }

    // DELETE /api/v1/organisations/{id} - Supprimer une organisation (Soft Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        organisationService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Organisation supprimee avec succes")
        );
    }
}
