package com.firstclub.membership.repository;

import com.firstclub.membership.domain.entity.UserSubscription;
import com.firstclub.membership.domain.enums.SubscriptionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    @Query("SELECT s FROM UserSubscription s WHERE s.user.id = :userId " +
           "AND s.status = 'ACTIVE' AND s.expiryDate > :now " +
           "ORDER BY s.expiryDate DESC")
    Optional<UserSubscription> findActiveSubscription(
        @Param("userId") Long userId,
        @Param("now") LocalDateTime now
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM UserSubscription s WHERE s.user.id = :userId " +
           "AND s.status = 'ACTIVE' AND s.expiryDate > :now")
    Optional<UserSubscription> findActiveSubscriptionWithLock(
        @Param("userId") Long userId,
        @Param("now") LocalDateTime now
    );

    @Query("SELECT s FROM UserSubscription s WHERE s.user.id = :userId ORDER BY s.createdAt DESC")
    List<UserSubscription> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT s FROM UserSubscription s WHERE s.status = :status " +
           "AND s.expiryDate <= :expiryDate")
    List<UserSubscription> findExpiredSubscriptions(
        @Param("status") SubscriptionStatus status,
        @Param("expiryDate") LocalDateTime expiryDate
    );
}
