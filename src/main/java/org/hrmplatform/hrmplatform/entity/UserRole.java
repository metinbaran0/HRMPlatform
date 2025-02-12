package org.hrmplatform.hrmplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hrmplatform.hrmplatform.enums.Role;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "tbl_roles")
@Entity
public class UserRole {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long userId;
	@Enumerated(EnumType.STRING)
	private Role role;
}