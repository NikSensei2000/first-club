package com.firstclub.membership.dto.response;

import com.firstclub.membership.domain.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {

    private Long id;
    private Long userId;
    private MembershipPlanResponse plan;
    private MembershipTierResponse tier;
    private SubscriptionStatus status;
    private LocalDateTime startDate;
    private LocalDateTime expiryDate;
    private BigDecimal paidAmount;
    private Integer orderCount;
    private BigDecimal totalOrderValue;
}
