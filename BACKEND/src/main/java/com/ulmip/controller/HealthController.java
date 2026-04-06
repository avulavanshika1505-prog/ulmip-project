package com.ulmip.controller;

import com.ulmip.model.HealthLog;
import com.ulmip.service.HealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final HealthService healthService;

    @GetMapping
    public ResponseEntity<List<HealthLog>> getAll() {
        return ResponseEntity.ok(healthService.getAllLogs());
    }

    @GetMapping("/latest")
    public ResponseEntity<?> getLatest() {
        return ResponseEntity.ok(healthService.getLatest().orElse(null));
    }

    @GetMapping("/averages")
    public ResponseEntity<Map<String, Double>> getAverages() {
        return ResponseEntity.ok(Map.of(
            "sleep", healthService.getAvgSleep(),
            "mood", healthService.getAvgMood(),
            "stress", healthService.getAvgStress()
        ));
    }

    @PostMapping
    public ResponseEntity<HealthLog> log(@RequestBody HealthLog log) {
        return ResponseEntity.ok(healthService.logHealth(log));
    }
}
