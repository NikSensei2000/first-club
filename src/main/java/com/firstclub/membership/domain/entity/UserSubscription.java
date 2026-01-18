package com.firstclub.membership.domain.entity;

import com.firstclub.membership.domain.enums.SubscriptionStatus;
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
@Table(name = "user_subscriptions", indexes = {
    @Index(name = "idx_user_status", columnList = "userId,status"),
    @Index(name = "idx_expiry", columnList = "expiryDate")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private MembershipPlan plan;

    @Column(name = "plan_id", insertable = false, updatable = false)
    private Long planId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @Column(name = "tier_id", insertable = false, updatable = false)
    private Long tierId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionStatus status;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal paidAmount;

    @Column(nullable = false)
    @Builder.Default
    private Integer orderCount = 0;

    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalOrderValue = BigDecimal.ZERO;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
