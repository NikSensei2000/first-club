package com.firstclub.membership.repository;

import com.firstclub.membership.domain.entity.TierBenefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TierBenefitRepository extends JpaRepository<TierBenefit, Long> {

    List<TierBenefit> findByTierIdAndActiveTrue(Long tierId);

    List<TierBenefit> findByTierIdInAndActiveTrue(List<Long> tierIds);
}
