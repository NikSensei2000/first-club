package com.firstclub.membership.domain.entity;

import com.firstclub.membership.domain.enums.BenefitType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tier_benefits", indexes = {
    @Index(name = "idx_tier_benefit", columnList = "tierId,benefitType")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TierBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @Column(name = "tier_id", insertable = false, updatable = false)
    private Long tierId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private BenefitType benefitType;

    @Column(length = 500)
    private String description;

    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(length = 200)
    private String applicableCategories;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
