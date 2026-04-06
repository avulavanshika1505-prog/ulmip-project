package com.ulmip.service;

import com.ulmip.model.HealthLog;
import com.ulmip.repository.HealthLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HealthService {

    private final HealthLogRepository healthLogRepository;

    public List<HealthLog> getAllLogs() {
        return healthLogRepository.findAllByOrderByDateDesc();
    }

    public Optional<HealthLog> getLatest() {
        return healthLogRepository.findTopByOrderByDateDesc();
    }

    public HealthLog logHealth(HealthLog log) {
        return healthLogRepository.save(log);
    }

    public double getAvgSleep() {
        Double v = healthLogRepository.avgSleep();
        return v != null ? Math.round(v * 10.0) / 10.0 : 0;
    }

    public double getAvgMood() {
        Double v = healthLogRepository.avgMood();
        return v != null ? Math.round(v * 10.0) / 10.0 : 0;
    }

    public double getAvgStress() {
        Double v = healthLogRepository.avgStress();
        return v != null ? Math.round(v * 10.0) / 10.0 : 0;
    }
}
