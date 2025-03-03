package org.hrmplatform.hrmplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hrmplatform.hrmplatform.enums.Status;
import org.hrmplatform.hrmplatform.enums.SubscriptionPlan;
import org.hrmplatform.hrmplatform.entity.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_company")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String contactPerson;
    
    private String sector;
    private Integer employeeCount;
    
    
    private Long userId;
    
    private boolean emailVerified; // Mail doğrulama durumu
    
    private String emailVerificationToken; //  Doğrulama tokeni
    private LocalDateTime tokenExpirationTime; // Token geçerlilik süresi
    
    @Enumerated(EnumType.STRING)
    private Status status; // Şirketin başvuru durumu (Onaylandı, Reddedildi, Beklemede)
    
    @Enumerated(EnumType.STRING)
    private SubscriptionPlan subscriptionPlan; // Aylık, Yıllık
    
    private LocalDateTime subscriptionEndDate; // Üyelik bitiş tarihi

    @Builder.Default
    private boolean isDeleted = false;  // Soft delete için alan
    
    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        status = Status.PENDING; // Başlangıçta başvuru "Beklemede" olacak
        // Üyelik bitiş tarihi hesaplaması
        if (subscriptionPlan != null) {
            if (subscriptionPlan == SubscriptionPlan.MONTHLY) {
                subscriptionEndDate = createdAt.plusMonths(1); // 1 ay sonrasını hesapla
            } else if (subscriptionPlan == SubscriptionPlan.YEARLY) {
                subscriptionEndDate = createdAt.plusYears(1); // 1 yıl sonrasını hesapla
            }
        }
        
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Üyelik planını ayarla ve bitiş tarihini güncelle
    public void setSubscriptionPlan(SubscriptionPlan plan) {
        this.subscriptionPlan = plan;
        LocalDateTime now = LocalDateTime.now();
        
        if (plan == SubscriptionPlan.MONTHLY) {
            this.subscriptionEndDate = now.plusMonths(1);
        } else if (plan == SubscriptionPlan.YEARLY) {
            this.subscriptionEndDate = now.plusYears(1);
        }
    }

    // Yeni Constructor: sadece ID ile Company oluşturuyor
    public Company(Long id) {
        this.id = id;
    }

}
   


