package com.firstclub.membership.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequest {

    @NotNull(message = "Plan ID is required")
    private Long planId;

    @NotNull(message = "Tier ID is required")
    private Long tierId;
}
