package com.ulmip.controller;

import com.ulmip.model.Goal;
import com.ulmip.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @GetMapping
    public ResponseEntity<List<Goal>> getAll() {
        return ResponseEntity.ok(goalService.getAll());
    }

    @PostMapping
    public ResponseEntity<Goal> create(@RequestBody Goal goal) {
        return ResponseEntity.ok(goalService.create(goal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Goal> update(@PathVariable Long id, @RequestBody Goal goal) {
        return ResponseEntity.ok(goalService.update(id, goal));
    }

    @PatchMapping("/{id}/progress")
    public ResponseEntity<Goal> updateProgress(@PathVariable Long id, @RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(goalService.updateProgress(id, body.get("progress")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        goalService.delete(id);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
