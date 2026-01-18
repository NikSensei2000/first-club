package com.firstclub.membership.service;

import com.firstclub.membership.domain.entity.MembershipPlan;
import com.firstclub.membership.dto.response.MembershipPlanResponse;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.MembershipPlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MembershipPlanService {

    private static final Logger logger = LoggerFactory.getLogger(MembershipPlanService.class);

    private final MembershipPlanRepository planRepository;

    public MembershipPlanService(MembershipPlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "membershipPlans", key = "'all'")
    public List<MembershipPlanResponse> getAllPlans() {
        logger.info("Fetching all active membership plans");
        logger.debug("Checking cache for membership plans");
        
        List<MembershipPlan> plans = planRepository.findByActiveTrue();
        logger.debug("Retrieved {} active plans from database", plans.size());
        
        List<MembershipPlanResponse> responses = plans.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        logger.info("Successfully fetched {} membership plans", responses.size());
        return responses;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "membershipPlans", key = "#id")
    public MembershipPlanResponse getPlanById(Long id) {
        logger.info("Fetching membership plan by id: {}", id);
        logger.debug("Checking cache for plan id: {}", id);
        
        MembershipPlan plan = planRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> {
                    logger.error("Membership plan not found with id: {}", id);
                    return new ResourceNotFoundException("MembershipPlan", "id", id);
                });
        
        logger.debug("Found plan: {} ({})", plan.getName(), plan.getDuration());
        logger.info("Successfully fetched membership plan: {}", plan.getName());
        return mapToResponse(plan);
    }

    private MembershipPlanResponse mapToResponse(MembershipPlan plan) {
        logger.trace("Mapping plan entity to response DTO: {}", plan.getName());
        return MembershipPlanResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .duration(plan.getDuration())
                .price(plan.getPrice())
                .active(plan.getActive())
                .build();
    }
}
