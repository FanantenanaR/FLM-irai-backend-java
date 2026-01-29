package com.flm.irai.faritra.controller;

import com.flm.irai.common.dto.ApiResponse;
import com.flm.irai.faritra.model.Faritra;
import com.flm.irai.faritra.service.FaritraService;
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
@RequestMapping("/api/v1/faritras")
@RequiredArgsConstructor
public class FaritraController {

    private final FaritraService faritraService;

    // GET /api/v1/faritras/{id} - Recuperer un faritra par ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Faritra>> getById(@PathVariable UUID id) {
        Faritra faritra = faritraService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Faritra recupere avec succes", faritra)
        );
    }

    // GET /api/v1/faritras - Liste paginee avec filtres optionnels
    @GetMapping
    public ResponseEntity<ApiResponse<List<Faritra>>> getAll(
            @RequestParam(required = false) UUID fiangonanaId,
            @RequestParam(required = false) String nom,
            @PageableDefault(size = 20, sort = "nom", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<Faritra> page = faritraService.getAll(fiangonanaId, nom, pageable);
        return ResponseEntity.ok(
                ApiResponse.successPaginated(
                        "Liste des faritra recuperee avec succes",
                        page.getContent(),
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements()
                )
        );
    }

    // POST /api/v1/faritras - Creer un faritra
    @PostMapping
    public ResponseEntity<ApiResponse<Faritra>> create(@Valid @RequestBody Faritra request) {
        Faritra faritra = faritraService.create(
                request.getNom(),
                request.getFiangonana() != null ? request.getFiangonana().getId() : null
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Faritra cree avec succes", faritra));
    }

    // PUT /api/v1/faritras/{id} - Modifier un faritra
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Faritra>> update(
            @PathVariable UUID id,
            @RequestBody Faritra request
    ) {
        Faritra faritra = faritraService.update(
                id,
                request.getNom(),
                request.getFiangonana() != null ? request.getFiangonana().getId() : null
        );
        return ResponseEntity.ok(
                ApiResponse.success("Faritra modifie avec succes", faritra)
        );
    }

    // DELETE /api/v1/faritras/{id} - Supprimer un faritra (Soft Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        faritraService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Faritra supprime avec succes")
        );
    }
}
