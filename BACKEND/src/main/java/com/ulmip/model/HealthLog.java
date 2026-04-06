package com.ulmip.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "health_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Builder.Default
    private LocalDate date = LocalDate.now();

    @DecimalMin("0") @DecimalMax("24")
    @Column
    @Builder.Default
    private double sleep = 7.0;

    @Min(0) @Max(20)
    @Column
    @Builder.Default
    private int water = 8;

    @Min(0) @Max(300)
    @Column
    @Builder.Default
    private int exercise = 0;  // minutes

    @Min(1) @Max(5)
    @Column
    @Builder.Default
    private int mood = 3;

    @Min(1) @Max(5)
    @Column
    @Builder.Default
    private int stress = 3;

    @Column
    private String notes;
}
