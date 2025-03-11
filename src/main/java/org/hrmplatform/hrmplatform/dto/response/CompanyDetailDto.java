package org.hrmplatform.hrmplatform.dto.response;

import lombok.Builder;
import org.hrmplatform.hrmplatform.enums.Status;
import org.hrmplatform.hrmplatform.enums.SubscriptionPlan;

import java.time.LocalDateTime;

@Builder
public record CompanyDetailDto(
        Long id,
        String name,
        String address,
        String phone,
        String email,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String contactPerson,
        String sector,
        Integer employeeCount,
        boolean emailVerified,
        String emailVerificationToken,
        LocalDateTime tokenExpirationTime,
        Status status,
        SubscriptionPlan subscriptionPlan,
        LocalDateTime subscriptionEndDate,
        boolean isDeleted,
        boolean isActive
) {}

