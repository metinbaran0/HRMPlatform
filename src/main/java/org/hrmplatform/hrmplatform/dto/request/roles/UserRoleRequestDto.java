package org.hrmplatform.hrmplatform.dto.request.roles;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.hrmplatform.hrmplatform.enums.Role;


public record UserRoleRequestDto(Long userId,
                                 @NotNull(message = "Role is required")
                                 Role role) {
}