package org.hrmplatform.hrmplatform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hrmplatform.hrmplatform.enums.SubscriptionPlan;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class SubscriptionResponse {
    private SubscriptionPlan plan;
    private LocalDateTime subscriptionEndDate;
}