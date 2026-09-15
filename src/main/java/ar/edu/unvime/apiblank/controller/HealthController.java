package ar.edu.unvime.apiblank.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

/** Expone un endpoint simple para verificar que el servicio está activo. */

@RestController
public class HealthController {

    @Operation(summary = "Verifica que el servicio está activo")
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
            "status", "ok",
            "service", "api-blank"
        );
    }
}