package org.hrmplatform.hrmplatform.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.hrmplatform.hrmplatform.enums.SubscriptionPlan;

@Getter
@Setter
public class SubscriptionPlanRequest {
    private SubscriptionPlan plan;
}
