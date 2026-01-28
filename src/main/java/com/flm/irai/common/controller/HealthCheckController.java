package com.flm.irai.common.controller;

import com.flm.irai.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controleur de verification de l'etat de sante de l'application.
 */
@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> healthCheck() {
        Map<String, String> healthData = Map.of(
                "application", "irai-backend",
                "status", "UP"
        );

        return ResponseEntity.ok(
                ApiResponse.success("Application en cours d'execution", healthData)
        );
    }

    @GetMapping("/ping")
    public ResponseEntity<ApiResponse<String>> ping() {
        return ResponseEntity.ok(
                ApiResponse.success("Pong", "pong")
        );
    }
}
