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
public class TierChangeRequest {

    @NotNull(message = "New tier ID is required")
    private Long newTierId;
}
