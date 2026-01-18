package com.firstclub.membership.controller;

import com.firstclub.membership.dto.response.ApiResponse;
import com.firstclub.membership.dto.response.MembershipTierResponse;
import com.firstclub.membership.service.MembershipTierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tiers")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Membership Tiers", description = "Membership tier management endpoints")
public class MembershipTierController {

    private final MembershipTierService tierService;

    public MembershipTierController(MembershipTierService tierService) {
        this.tierService = tierService;
    }

    @GetMapping
    @Operation(summary = "Get all membership tiers", description = "Retrieve all active membership tiers with benefits")
    public ResponseEntity<ApiResponse<List<MembershipTierResponse>>> getAllTiers() {
        List<MembershipTierResponse> tiers = tierService.getAllTiers();
        return ResponseEntity.ok(ApiResponse.success(tiers));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get membership tier by ID", description = "Retrieve a specific membership tier with benefits")
    public ResponseEntity<ApiResponse<MembershipTierResponse>> getTierById(@PathVariable Long id) {
        MembershipTierResponse tier = tierService.getTierById(id);
        return ResponseEntity.ok(ApiResponse.success(tier));
    }
}
