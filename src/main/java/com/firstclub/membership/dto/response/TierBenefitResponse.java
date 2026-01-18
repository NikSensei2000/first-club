package com.firstclub.membership.dto.response;

import com.firstclub.membership.domain.enums.BenefitType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TierBenefitResponse {

    private Long id;
    private BenefitType benefitType;
    private String description;
    private BigDecimal discountPercentage;
    private String applicableCategories;
}
