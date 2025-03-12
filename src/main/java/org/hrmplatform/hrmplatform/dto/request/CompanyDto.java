package org.hrmplatform.hrmplatform.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hrmplatform.hrmplatform.enums.SubscriptionPlan;

public record CompanyDto(
        @Size(min = 2, max = 100)
        String name,


        @Size(min = 5, max = 200)
        String address,

        @Size(min = 10, max = 15)
        String phone,

        @Email
        String email,

        SubscriptionPlan subscriptionPlan,
        String contactPerson,

        String sector,
        Integer employeeCount


) {
}
