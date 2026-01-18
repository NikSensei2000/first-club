package com.firstclub.membership.service;

import com.firstclub.membership.domain.entity.*;
import com.firstclub.membership.domain.enums.SubscriptionStatus;
import com.firstclub.membership.dto.request.OrderUpdateRequest;
import com.firstclub.membership.dto.request.SubscriptionRequest;
import com.firstclub.membership.dto.request.TierChangeRequest;
import com.firstclub.membership.dto.response.MembershipPlanResponse;
import com.firstclub.membership.dto.response.MembershipTierResponse;
import com.firstclub.membership.dto.response.SubscriptionResponse;
import com.firstclub.membership.exception.BusinessException;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionService.class);

    private final UserSubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final MembershipPlanRepository planRepository;
    private final MembershipTierRepository tierRepository;
    private final MembershipPlanService planService;
    private final MembershipTierService tierService;

    public SubscriptionService(UserSubscriptionRepository subscriptionRepository,
                              UserRepository userRepository,
                              MembershipPlanRepository planRepository,
                              MembershipTierRepository tierRepository,
                              MembershipPlanService planService,
                              MembershipTierService tierService) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.planRepository = planRepository;
        this.tierRepository = tierRepository;
        this.planService = planService;
        this.tierService = tierService;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @CacheEvict(value = "userSubscriptions", key = "#userId")
    public SubscriptionResponse subscribe(Long userId, SubscriptionRequest request) {
        logger.info("Starting subscription creation for userId: {}, planId: {}, tierId: {}", 
                    userId, request.getPlanId(), request.getTierId());
        logger.debug("Acquiring pessimistic lock on user record for userId: {}", userId);

        User user = userRepository.findByIdWithLock(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with id: {}", userId);
                    return new ResourceNotFoundException("User", "id", userId);
                });
        
        logger.debug("User found: {} (cohort: {})", user.getUsername(), user.getCohort());
        logger.debug("Checking for existing active subscription for userId: {}", userId);

        UserSubscription existingSubscription = subscriptionRepository
                .findActiveSubscriptionWithLock(userId, LocalDateTime.now())
                .orElse(null);

        if (existingSubscription != null) {
            logger.warn("Subscription creation failed - user {} already has active subscription: {}", 
                       userId, existingSubscription.getId());
            throw new BusinessException("User already has an active subscription");
        }

        logger.debug("No active subscription found, proceeding with creation");
        logger.debug("Fetching membership plan with id: {}", request.getPlanId());
        
        MembershipPlan plan = planRepository.findByIdAndActiveTrue(request.getPlanId())
                .orElseThrow(() -> {
                    logger.error("Membership plan not found or inactive with id: {}", request.getPlanId());
                    return new ResourceNotFoundException("MembershipPlan", "id", request.getPlanId());
                });
        
        logger.debug("Plan found: {} ({}, price: {})", plan.getName(), plan.getDuration(), plan.getPrice());
        logger.debug("Fetching membership tier with id: {}", request.getTierId());

        MembershipTier tier = tierRepository.findByIdAndActiveTrue(request.getTierId())
                .orElseThrow(() -> {
                    logger.error("Membership tier not found or inactive with id: {}", request.getTierId());
                    return new ResourceNotFoundException("MembershipTier", "id", request.getTierId());
                });
        
        logger.debug("Tier found: {} (level: {})", tier.getName(), tier.getTierLevel());

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime expiryDate = startDate.plusMonths(plan.getDuration().getMonths());
        logger.debug("Subscription dates - start: {}, expiry: {}", startDate, expiryDate);

        UserSubscription subscription = UserSubscription.builder()
                .user(user)
                .plan(plan)
                .tier(tier)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(startDate)
                .expiryDate(expiryDate)
                .paidAmount(plan.getPrice())
                .orderCount(0)
                .totalOrderValue(BigDecimal.ZERO)
                .build();

        logger.debug("Saving subscription to database");
        subscription = subscriptionRepository.save(subscription);
        logger.info("Subscription created successfully - subscriptionId: {}, userId: {}, plan: {}, tier: {}", 
                   subscription.getId(), userId, plan.getName(), tier.getName());
        logger.debug("Evicting cache for userId: {}", userId);

        return mapToResponse(subscription);
    }

    @Transactional(readOnly = true)
    public SubscriptionResponse getCurrentSubscription(Long userId) {
        logger.info("Fetching current active subscription for userId: {}", userId);
        logger.debug("Checking cache for user subscription: {}", userId);

        UserSubscription subscription = subscriptionRepository
                .findActiveSubscription(userId, LocalDateTime.now())
                .orElseThrow(() -> {
                    logger.warn("No active subscription found for userId: {}", userId);
                    return new ResourceNotFoundException("No active subscription found for user");
                });

        logger.debug("Active subscription found - subscriptionId: {}, tier: {}, status: {}, expiry: {}", 
                    subscription.getId(), subscription.getTier().getName(), 
                    subscription.getStatus(), subscription.getExpiryDate());
        logger.info("Successfully fetched subscription for userId: {}", userId);

        return mapToResponse(subscription);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getSubscriptionHistory(Long userId) {
        logger.info("Fetching subscription history for userId: {}", userId);

        List<UserSubscription> subscriptions = subscriptionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        logger.debug("Found {} subscriptions in history for userId: {}", subscriptions.size(), userId);

        List<SubscriptionResponse> responses = subscriptions.stream()
                .map(sub -> {
                    logger.trace("Mapping subscription: {} (status: {})", sub.getId(), sub.getStatus());
                    return mapToResponse(sub);
                })
                .collect(Collectors.toList());

        logger.info("Successfully fetched {} subscriptions for userId: {}", responses.size(), userId);
        return responses;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @CacheEvict(value = "userSubscriptions", key = "#userId")
    public SubscriptionResponse changeTier(Long userId, TierChangeRequest request) {
        logger.info("Starting tier change for userId: {}, newTierId: {}", userId, request.getNewTierId());
        logger.debug("Acquiring pessimistic lock on subscription for userId: {}", userId);

        UserSubscription subscription = subscriptionRepository
                .findActiveSubscriptionWithLock(userId, LocalDateTime.now())
                .orElseThrow(() -> {
                    logger.error("No active subscription found for userId: {} during tier change", userId);
                    return new ResourceNotFoundException("No active subscription found for user");
                });

        logger.debug("Current subscription - id: {}, currentTier: {} (level: {})", 
                    subscription.getId(), subscription.getTier().getName(), 
                    subscription.getTier().getTierLevel());
        logger.debug("Fetching new tier with id: {}", request.getNewTierId());

        MembershipTier newTier = tierRepository.findByIdAndActiveTrue(request.getNewTierId())
                .orElseThrow(() -> {
                    logger.error("Membership tier not found or inactive with id: {}", request.getNewTierId());
                    return new ResourceNotFoundException("MembershipTier", "id", request.getNewTierId());
                });

        logger.debug("New tier found: {} (level: {})", newTier.getName(), newTier.getTierLevel());

        if (subscription.getTier().getId().equals(newTier.getId())) {
            logger.warn("Tier change failed - user {} already on tier: {}", userId, newTier.getName());
            throw new BusinessException("User is already on this tier");
        }

        String oldTierName = subscription.getTier().getName();
        subscription.setTier(newTier);
        
        logger.debug("Saving tier change to database");
        subscription = subscriptionRepository.save(subscription);
        
        logger.info("Tier changed successfully for subscriptionId: {} - {} -> {}", 
                   subscription.getId(), oldTierName, newTier.getName());
        logger.debug("Evicting cache for userId: {}", userId);

        return mapToResponse(subscription);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @CacheEvict(value = "userSubscriptions", key = "#userId")
    public void cancelSubscription(Long userId) {
        logger.info("Starting subscription cancellation for userId: {}", userId);
        logger.debug("Acquiring pessimistic lock on subscription for userId: {}", userId);

        UserSubscription subscription = subscriptionRepository
                .findActiveSubscriptionWithLock(userId, LocalDateTime.now())
                .orElseThrow(() -> {
                    logger.error("No active subscription found for userId: {} during cancellation", userId);
                    return new ResourceNotFoundException("No active subscription found for user");
                });

        logger.debug("Found active subscription - id: {}, plan: {}, tier: {}", 
                    subscription.getId(), subscription.getPlan().getName(), 
                    subscription.getTier().getName());

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        
        logger.debug("Saving cancellation status to database");
        subscriptionRepository.save(subscription);

        logger.info("Subscription cancelled successfully - subscriptionId: {}, userId: {}", 
                   subscription.getId(), userId);
        logger.debug("Evicting cache for userId: {}", userId);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @CacheEvict(value = "userSubscriptions", key = "#userId")
    public SubscriptionResponse updateOrderStats(Long userId, OrderUpdateRequest request) {
        logger.info("Starting order stats update for userId: {}, orderValue: {}", 
                   userId, request.getOrderValue());
        logger.debug("Acquiring pessimistic lock on subscription for userId: {}", userId);

        UserSubscription subscription = subscriptionRepository
                .findActiveSubscriptionWithLock(userId, LocalDateTime.now())
                .orElseThrow(() -> {
                    logger.error("No active subscription found for userId: {} during order update", userId);
                    return new ResourceNotFoundException("No active subscription found for user");
                });

        int oldOrderCount = subscription.getOrderCount();
        BigDecimal oldOrderValue = subscription.getTotalOrderValue();
        String currentTier = subscription.getTier().getName();

        logger.debug("Current stats - orders: {}, totalValue: {}, tier: {}", 
                    oldOrderCount, oldOrderValue, currentTier);

        subscription.setOrderCount(subscription.getOrderCount() + 1);
        subscription.setTotalOrderValue(subscription.getTotalOrderValue().add(request.getOrderValue()));

        logger.debug("Updated stats - orders: {}, totalValue: {}", 
                    subscription.getOrderCount(), subscription.getTotalOrderValue());
        logger.debug("Fetching user details for tier eligibility check");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with id: {} during order update", userId);
                    return new ResourceNotFoundException("User", "id", userId);
                });

        logger.debug("Checking tier eligibility for user: {} (cohort: {})", 
                    user.getUsername(), user.getCohort());

        MembershipTier eligibleTier = tierService.findEligibleTier(
                subscription.getOrderCount(),
                subscription.getTotalOrderValue(),
                user.getCohort()
        );

        logger.debug("Eligible tier determined: {} (level: {})", 
                    eligibleTier.getName(), eligibleTier.getTierLevel());

        if (eligibleTier.getTierLevel() > subscription.getTier().getTierLevel()) {
            logger.info("Tier upgrade triggered for userId: {} - {} (level {}) -> {} (level {})", 
                       userId, currentTier, subscription.getTier().getTierLevel(),
                       eligibleTier.getName(), eligibleTier.getTierLevel());
            subscription.setTier(eligibleTier);
        } else {
            logger.debug("No tier upgrade - user remains on tier: {}", currentTier);
        }

        logger.debug("Saving updated subscription to database");
        subscription = subscriptionRepository.save(subscription);
        
        logger.info("Order stats updated successfully - subscriptionId: {}, orders: {} -> {}, value: {} -> {}, tier: {}", 
                   subscription.getId(), oldOrderCount, subscription.getOrderCount(),
                   oldOrderValue, subscription.getTotalOrderValue(), 
                   subscription.getTier().getName());
        logger.debug("Evicting cache for userId: {}", userId);

        return mapToResponse(subscription);
    }

    private SubscriptionResponse mapToResponse(UserSubscription subscription) {
        logger.trace("Mapping subscription entity to response DTO - subscriptionId: {}", subscription.getId());
        
        MembershipPlanResponse planResponse = planService.getPlanById(subscription.getPlan().getId());
        MembershipTierResponse tierResponse = tierService.getTierById(subscription.getTier().getId());

        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .userId(subscription.getUser().getId())
                .plan(planResponse)
                .tier(tierResponse)
                .status(subscription.getStatus())
                .startDate(subscription.getStartDate())
                .expiryDate(subscription.getExpiryDate())
                .paidAmount(subscription.getPaidAmount())
                .orderCount(subscription.getOrderCount())
                .totalOrderValue(subscription.getTotalOrderValue())
                .build();
    }
}
