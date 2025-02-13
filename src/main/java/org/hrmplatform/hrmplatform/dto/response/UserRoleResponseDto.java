package org.hrmplatform.hrmplatform.dto.response;

import java.util.List;

public record UserRoleResponseDto(String name, List<String> roles) {
}