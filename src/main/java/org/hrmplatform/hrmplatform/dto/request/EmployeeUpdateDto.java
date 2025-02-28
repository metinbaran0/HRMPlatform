package org.hrmplatform.hrmplatform.dto.request;

import jakarta.validation.constraints.*;

public record EmployeeUpdateDto(

        String avatar,


        String name,


        String surname,


        @Email(message = "Invalid email format")
        String email,


        @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{10}$", message = "Invalid phone number format")
        String phone,


        String position
) {
}
