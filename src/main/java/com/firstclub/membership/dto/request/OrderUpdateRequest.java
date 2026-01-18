package com.firstclub.membership.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateRequest {

    @NotNull(message = "Order value is required")
    @Positive(message = "Order value must be positive")
    private BigDecimal orderValue;
}
