package org.hrmplatform.hrmplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_employee")
public class Employee{
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long companyId;
    private String avatar;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String position;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}