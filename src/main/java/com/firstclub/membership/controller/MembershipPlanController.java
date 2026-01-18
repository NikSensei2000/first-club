package com.firstclub.membership.controller;

import com.firstclub.membership.dto.response.ApiResponse;
import com.firstclub.membership.dto.response.MembershipPlanResponse;
import com.firstclub.membership.service.MembershipPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Membership Plans", description = "Membership plan management endpoints")
public class MembershipPlanController {

    private final MembershipPlanService planService;

    public MembershipPlanController(MembershipPlanService planService) {
        this.planService = planService;
    }

    @GetMapping
    @Operation(summary = "Get all membership plans", description = "Retrieve all active membership plans")
    public ResponseEntity<ApiResponse<List<MembershipPlanResponse>>> getAllPlans() {
        List<MembershipPlanResponse> plans = planService.getAllPlans();
        return ResponseEntity.ok(ApiResponse.success(plans));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get membership plan by ID", description = "Retrieve a specific membership plan")
    public ResponseEntity<ApiResponse<MembershipPlanResponse>> getPlanById(@PathVariable Long id) {
        MembershipPlanResponse plan = planService.getPlanById(id);
        return ResponseEntity.ok(ApiResponse.success(plan));
    }
}
