package org.hrmplatform.hrmplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_employeeshift")
public class EmployeeShift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long employeeId;
    private Long shiftId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private LocalDate shiftDate; // shiftDate olarak değiştirilmiş
    private Long companyId;


    private Boolean isActive;
    @Builder.Default
    private Boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (shiftDate == null) {
            shiftDate = LocalDate.now(); // Eğer shiftDate null ise, güncel tarih atansın
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
