package com.festibuy.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.festibuy.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health", description = "Endpoints for checking system health and uptime")
public class HealthController {

    @GetMapping
    @Operation(summary = "Check service health", description = "Returns the operational status and version of the backend service")
    public ResponseEntity<ApiResponse<Map<String, String>>> getHealthStatus() {
        Map<String, String> statusData = Map.of(
            "status", "UP",
            "service", "festibuy-backend",
            "version", "0.0.1-SNAPSHOT"
        );
        return ResponseEntity.ok(ApiResponse.success("Application is healthy and running", statusData));
    }
}


