package org.hrmplatform.hrmplatform.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hrmplatform.hrmplatform.enums.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
	private String name;
	private String email;
	private String password;
	private Role role;
}