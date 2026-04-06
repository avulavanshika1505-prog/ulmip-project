package com.ulmip.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Category category = Category.PERSONAL;

    @Positive
    @Column(nullable = false)
    private double target;

    @Column(nullable = false)
    @Builder.Default
    private double progress = 0;

    @Column
    @Builder.Default
    private String unit = "%";

    @Column
    private LocalDate deadline;

    @Column
    private String description;

    public enum Category { ACADEMIC, HEALTH, FINANCE, PERSONAL, WORK, FITNESS }

    public boolean isCompleted() {
        return progress >= target;
    }

    public int getProgressPercent() {
        return (int) Math.min((progress / target) * 100, 100);
    }
}
