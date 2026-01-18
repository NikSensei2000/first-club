package com.firstclub.membership.dto.response;

import com.firstclub.membership.domain.enums.PlanDuration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipPlanResponse {

    private Long id;
    private String name;
    private String description;
    private PlanDuration duration;
    private BigDecimal price;
    private Boolean active;
}
