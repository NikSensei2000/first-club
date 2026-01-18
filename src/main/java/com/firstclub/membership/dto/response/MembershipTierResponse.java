package com.firstclub.membership.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipTierResponse {

    private Long id;
    private String name;
    private String description;
    private Integer tierLevel;
    private Integer minOrderCount;
    private BigDecimal minOrderValue;
    private String requiredCohort;
    private Boolean active;
    private List<TierBenefitResponse> benefits;
}
