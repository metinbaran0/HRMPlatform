package org.hrmplatform.hrmplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hrmplatform.hrmplatform.enums.ShiftType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_shift")
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long companyId;
    private String shiftName;
    private LocalDate startTime;
    private LocalDate endTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Integer durationInMinutes; //vardiya süresini hesaplamak için ayrı bir alan
    private boolean deleted = false; // Soft delete

    @Enumerated(EnumType.STRING)
    private ShiftType shiftType;  // Sabah, Akşam, Gece veya Saatlik Vardiya tipi



    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        this.durationInMinutes = (int) java.time.Duration.between(startTime, endTime).toMinutes();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


}

