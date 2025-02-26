package org.hrmplatform.hrmplatform.dto.request;


import jakarta.validation.constraints.*;

public record EmployeeRequestDto (
    @NotNull(message = "Company ID cannot be null")
    Long companyId,

    @NotBlank(message = "Name cannot be empty")
    String name,

    @NotBlank(message = "Surname cannot be empty")
    String surname,

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    String email,

    @NotBlank(message = "Phone number cannot be empty")
    @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{10}$", message = "Invalid phone number format")
    String phone,

    @NotBlank(message = "Position cannot be empty")
    String position
){}
