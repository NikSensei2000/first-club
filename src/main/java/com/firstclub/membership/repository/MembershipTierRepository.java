package com.firstclub.membership.repository;

import com.firstclub.membership.domain.entity.MembershipTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipTierRepository extends JpaRepository<MembershipTier, Long> {

    List<MembershipTier> findByActiveTrueOrderByTierLevelAsc();

    Optional<MembershipTier> findByIdAndActiveTrue(Long id);

    @Query("SELECT t FROM MembershipTier t WHERE t.active = true " +
           "AND t.minOrderCount <= :orderCount " +
           "AND t.minOrderValue <= :orderValue " +
           "AND (t.requiredCohort IS NULL OR t.requiredCohort = :cohort) " +
           "ORDER BY t.tierLevel DESC")
    List<MembershipTier> findEligibleTiers(
        @Param("orderCount") Integer orderCount,
        @Param("orderValue") BigDecimal orderValue,
        @Param("cohort") String cohort
    );
}
