package com.firstclub.membership.domain.entity;

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
@Table(name = "membership_tiers", indexes = {
    @Index(name = "idx_tier_level", columnList = "tierLevel")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Integer tierLevel;

    @Column(nullable = false)
    private Integer minOrderCount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal minOrderValue;

    @Column(length = 50)
    private String requiredCohort;

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
