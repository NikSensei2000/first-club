package com.firstclub.membership.controller;

import com.firstclub.membership.dto.request.OrderUpdateRequest;
import com.firstclub.membership.dto.request.SubscriptionRequest;
import com.firstclub.membership.dto.request.TierChangeRequest;
import com.firstclub.membership.dto.response.ApiResponse;
import com.firstclub.membership.dto.response.SubscriptionResponse;
import com.firstclub.membership.security.UserPrincipal;
import com.firstclub.membership.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Subscriptions", description = "User subscription management endpoints")
public class SubscriptionController {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    @Operation(summary = "Subscribe to a plan", description = "Create a new subscription for the authenticated user")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> subscribe(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody SubscriptionRequest request) {
        logger.info("Received subscription request from userId: {}, planId: {}, tierId: {}", 
                   userPrincipal.getId(), request.getPlanId(), request.getTierId());
        
        SubscriptionResponse response = subscriptionService.subscribe(userPrincipal.getId(), request);
        
        logger.info("Subscription created successfully for userId: {}", userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Subscription created successfully", response));
    }

    @GetMapping("/current")
    @Operation(summary = "Get current subscription", description = "Retrieve the active subscription for the authenticated user")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getCurrentSubscription(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        logger.info("Received request to get current subscription for userId: {}", userPrincipal.getId());
        
        SubscriptionResponse response = subscriptionService.getCurrentSubscription(userPrincipal.getId());
        
        logger.debug("Returning current subscription for userId: {}", userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/history")
    @Operation(summary = "Get subscription history", description = "Retrieve all subscriptions for the authenticated user")
    public ResponseEntity<ApiResponse<List<SubscriptionResponse>>> getSubscriptionHistory(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        logger.info("Received request to get subscription history for userId: {}", userPrincipal.getId());
        
        List<SubscriptionResponse> response = subscriptionService.getSubscriptionHistory(userPrincipal.getId());
        
        logger.debug("Returning {} subscriptions for userId: {}", response.size(), userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/tier")
    @Operation(summary = "Change membership tier", description = "Upgrade or downgrade the membership tier")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> changeTier(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody TierChangeRequest request) {
        logger.info("Received tier change request from userId: {}, newTierId: {}", 
                   userPrincipal.getId(), request.getNewTierId());
        
        SubscriptionResponse response = subscriptionService.changeTier(userPrincipal.getId(), request);
        
        logger.info("Tier changed successfully for userId: {}", userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Tier changed successfully", response));
    }

    @DeleteMapping
    @Operation(summary = "Cancel subscription", description = "Cancel the active subscription")
    public ResponseEntity<ApiResponse<Void>> cancelSubscription(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        logger.info("Received subscription cancellation request from userId: {}", userPrincipal.getId());
        
        subscriptionService.cancelSubscription(userPrincipal.getId());
        
        logger.info("Subscription cancelled successfully for userId: {}", userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Subscription cancelled successfully", null));
    }

    @PostMapping("/order")
    @Operation(summary = "Update order statistics", description = "Record a new order and update subscription statistics")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> updateOrderStats(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody OrderUpdateRequest request) {
        logger.info("Received order update request from userId: {}, orderValue: {}", 
                   userPrincipal.getId(), request.getOrderValue());
        
        SubscriptionResponse response = subscriptionService.updateOrderStats(userPrincipal.getId(), request);
        
        logger.info("Order statistics updated successfully for userId: {}", userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Order statistics updated successfully", response));
    }
}
