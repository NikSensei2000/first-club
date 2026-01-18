package com.firstclub.membership.service;

import com.firstclub.membership.domain.entity.MembershipTier;
import com.firstclub.membership.domain.entity.TierBenefit;
import com.firstclub.membership.dto.response.MembershipTierResponse;
import com.firstclub.membership.dto.response.TierBenefitResponse;
import com.firstclub.membership.exception.BusinessException;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.MembershipTierRepository;
import com.firstclub.membership.repository.TierBenefitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MembershipTierService {

    private static final Logger logger = LoggerFactory.getLogger(MembershipTierService.class);

    private final MembershipTierRepository tierRepository;
    private final TierBenefitRepository benefitRepository;

    public MembershipTierService(MembershipTierRepository tierRepository,
                                TierBenefitRepository benefitRepository) {
        this.tierRepository = tierRepository;
        this.benefitRepository = benefitRepository;
    }

    @Transactional(readOnly = true)
    public List<MembershipTierResponse> getAllTiers() {
        logger.info("Fetching all active membership tiers with benefits");
        logger.debug("Checking cache for membership tiers");
        
        List<MembershipTier> tiers = tierRepository.findByActiveTrueOrderByTierLevelAsc();
        logger.debug("Retrieved {} active tiers from database", tiers.size());
        
        List<Long> tierIds = tiers.stream().map(MembershipTier::getId).collect(Collectors.toList());
        logger.debug("Fetching benefits for tier IDs: {}", tierIds);
        
        Map<Long, List<TierBenefit>> benefitsByTier = benefitRepository.findByTierIdInAndActiveTrue(tierIds)
                .stream()
                .collect(Collectors.groupingBy(TierBenefit::getTierId));
        
        logger.debug("Retrieved benefits for {} tiers", benefitsByTier.size());

        List<MembershipTierResponse> responses = tiers.stream()
                .map(tier -> {
                    List<TierBenefit> benefits = benefitsByTier.get(tier.getId());
                    logger.trace("Tier {} has {} benefits", tier.getName(), benefits != null ? benefits.size() : 0);
                    return mapToResponse(tier, benefits);
                })
                .collect(Collectors.toList());
        
        logger.info("Successfully fetched {} membership tiers with benefits", responses.size());
        return responses;
    }

    @Transactional(readOnly = true)
    public MembershipTierResponse getTierById(Long id) {
        logger.info("Fetching membership tier by id: {}", id);
        logger.debug("Checking cache for tier id: {}", id);
        
        MembershipTier tier = tierRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> {
                    logger.error("Membership tier not found with id: {}", id);
                    return new ResourceNotFoundException("MembershipTier", "id", id);
                });
        
        logger.debug("Found tier: {} (level {})", tier.getName(), tier.getTierLevel());
        logger.debug("Fetching benefits for tier: {}", tier.getName());
        
        List<TierBenefit> benefits = benefitRepository.findByTierIdAndActiveTrue(id);
        logger.debug("Retrieved {} benefits for tier: {}", benefits.size(), tier.getName());
        
        logger.info("Successfully fetched membership tier: {}", tier.getName());
        return mapToResponse(tier, benefits);
    }

    @Transactional(readOnly = true)
    public MembershipTier findEligibleTier(Integer orderCount, BigDecimal orderValue, String cohort) {
        logger.info("Finding eligible tier for orderCount: {}, orderValue: {}, cohort: {}", 
                    orderCount, orderValue, cohort);
        
        logger.debug("Querying database for eligible tiers with criteria - orders: {}, value: {}, cohort: {}", 
                    orderCount, orderValue, cohort);
        
        List<MembershipTier> eligibleTiers = tierRepository.findEligibleTiers(orderCount, orderValue, cohort);
        logger.debug("Found {} eligible tiers", eligibleTiers.size());
        
        if (eligibleTiers.isEmpty()) {
            logger.warn("No eligible tier found for criteria, returning default Silver tier");
            List<MembershipTier> allTiers = tierRepository.findByActiveTrueOrderByTierLevelAsc();
            if (allTiers.isEmpty()) {
                logger.error("No active tiers found in database");
                throw new BusinessException("No active tiers available");
            }
            MembershipTier defaultTier = allTiers.get(0);
            logger.info("Returning default tier: {}", defaultTier.getName());
            return defaultTier;
        }
        
        MembershipTier selectedTier = eligibleTiers.get(0);
        logger.info("Selected eligible tier: {} (level {})", selectedTier.getName(), selectedTier.getTierLevel());
        return selectedTier;
    }

    private MembershipTierResponse mapToResponse(MembershipTier tier, List<TierBenefit> benefits) {
        logger.trace("Mapping tier entity to response DTO: {}", tier.getName());
        
        List<TierBenefitResponse> benefitResponses = benefits != null ? 
                benefits.stream()
                    .map(this::mapBenefitToResponse)
                    .collect(Collectors.toList()) : 
                List.of();

        return MembershipTierResponse.builder()
                .id(tier.getId())
                .name(tier.getName())
                .description(tier.getDescription())
                .tierLevel(tier.getTierLevel())
                .minOrderCount(tier.getMinOrderCount())
                .minOrderValue(tier.getMinOrderValue())
                .requiredCohort(tier.getRequiredCohort())
                .active(tier.getActive())
                .benefits(benefitResponses)
                .build();
    }

    private TierBenefitResponse mapBenefitToResponse(TierBenefit benefit) {
        logger.trace("Mapping benefit entity to response DTO: {}", benefit.getBenefitType());
        return TierBenefitResponse.builder()
                .id(benefit.getId())
                .benefitType(benefit.getBenefitType())
                .description(benefit.getDescription())
                .discountPercentage(benefit.getDiscountPercentage())
                .applicableCategories(benefit.getApplicableCategories())
                .build();
    }
}
