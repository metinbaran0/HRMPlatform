package org.hrmplatform.hrmplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hrmplatform.hrmplatform.enums.ShiftType;

import java.time.Duration;
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
        calculateDuration();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Vardiya süresini hesaplamak için yardımcı metod
    public void calculateDuration() {
        // Eğer sadece tarih bilgisi varsa, saati 00:00:00 kabul ederiz
        LocalDateTime startDateTime = startTime.atStartOfDay(); // Start tarihinin 00:00:00'da olduğunu varsayıyoruz
        LocalDateTime endDateTime = endTime.atStartOfDay(); // End tarihinin 00:00:00'da olduğunu varsayıyoruz

        // Eğer saat bilgisi de varsa, startTime ve endTime'a saat bilgisi ekleyerek doğru hesaplama yapabilirsiniz
        Duration duration = Duration.between(startDateTime, endDateTime);
        this.durationInMinutes = (int) duration.toMinutes();
    }


}

