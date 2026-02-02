package com.flm.irai.sampana.controller;

import com.flm.irai.common.dto.ApiResponse;
import com.flm.irai.sampana.model.Sampana;
import com.flm.irai.sampana.service.SampanaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sampanas")
@RequiredArgsConstructor
public class SampanaController {

    private final SampanaService sampanaService;

    // GET /api/v1/sampanas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Sampana>> getById(@PathVariable UUID id) {
        Sampana sampana = sampanaService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Sampana recupere avec succes", sampana)
        );
    }

    // GET /api/v1/sampanas - Liste paginee avec filtres
    @GetMapping
    public ResponseEntity<ApiResponse<List<Sampana>>> getAll(
            @RequestParam(required = false) UUID fiangonanaId,
            @RequestParam(required = false) UUID typeId,
            @PageableDefault(size = 20, sort = "nom", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<Sampana> page = sampanaService.getAll(fiangonanaId, typeId, pageable);
        return ResponseEntity.ok(
                ApiResponse.successPaginated(
                        "Liste des sampana recuperee avec succes",
                        page.getContent(),
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements()
                )
        );
    }

    // PUT /api/v1/sampanas/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Sampana>> update(
            @PathVariable UUID id,
            @Valid @RequestBody Sampana request
    ) {
        Sampana sampana = sampanaService.update(
                id,
                request.getNom(),
                request.getType() != null ? request.getType().getId() : null,
                request.getDescription()
        );
        return ResponseEntity.ok(
                ApiResponse.success("Sampana modifie avec succes", sampana)
        );
    }

    // DELETE /api/v1/sampanas/{id} - Soft Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        sampanaService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Sampana supprime avec succes")
        );
    }
}
