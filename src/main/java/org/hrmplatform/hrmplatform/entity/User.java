package org.hrmplatform.hrmplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hrmplatform.hrmplatform.enums.Role;

import java.time.LocalDateTime;

//proje oluşturuldu
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private Boolean status;
    private Long companyId;
    private Long employeeId;
    
    @Column(name = "is_activated", nullable = false)
    private Boolean activated = false;
    

    
    @Column(name = "activation_code")
    private String activationCode;
    
    @Column(name = "activation_code_expire_at")
    private LocalDateTime activationCodeExpireAt;
    
    //reset token ve geçerlilik süresi
    @Column(name = "reset_token")
    private String resetToken;
    
    @Column(name = "reset_token_expire_at")
    private LocalDateTime resetTokenExpireAt;
    
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
    
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}