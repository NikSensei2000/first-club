package com.firstclub.membership.service;

import com.firstclub.membership.domain.entity.*;
import com.firstclub.membership.domain.enums.PlanDuration;
import com.firstclub.membership.domain.enums.SubscriptionStatus;
import com.firstclub.membership.dto.request.OrderUpdateRequest;
import com.firstclub.membership.dto.request.SubscriptionRequest;
import com.firstclub.membership.dto.response.SubscriptionResponse;
import com.firstclub.membership.exception.BusinessException;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private UserSubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MembershipPlanRepository planRepository;

    @Mock
    private MembershipTierRepository tierRepository;

    @Mock
    private MembershipPlanService planService;

    @Mock
    private MembershipTierService tierService;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private User testUser;
    private MembershipPlan testPlan;
    private MembershipTier testTier;
    private UserSubscription testSubscription;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .cohort("STANDARD")
                .build();

        testPlan = MembershipPlan.builder()
                .id(1L)
                .name("Monthly Basic")
                .duration(PlanDuration.MONTHLY)
                .price(new BigDecimal("9.99"))
                .active(true)
                .build();

        testTier = MembershipTier.builder()
                .id(1L)
                .name("Silver")
                .tierLevel(1)
                .minOrderCount(0)
                .minOrderValue(BigDecimal.ZERO)
                .active(true)
                .build();

        testSubscription = UserSubscription.builder()
                .id(1L)
                .user(testUser)
                .plan(testPlan)
                .tier(testTier)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusMonths(1))
                .paidAmount(new BigDecimal("9.99"))
                .orderCount(0)
                .totalOrderValue(BigDecimal.ZERO)
                .build();
    }

    @Test
    void subscribe_Success() {
        SubscriptionRequest request = SubscriptionRequest.builder()
                .planId(1L)
                .tierId(1L)
                .build();

        when(userRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionRepository.findActiveSubscriptionWithLock(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(planRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(testPlan));
        when(tierRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(testTier));
        when(subscriptionRepository.save(any(UserSubscription.class))).thenReturn(testSubscription);

        assertDoesNotThrow(() -> subscriptionService.subscribe(1L, request));

        verify(subscriptionRepository, times(1)).save(any(UserSubscription.class));
    }

    @Test
    void subscribe_UserAlreadyHasActiveSubscription_ThrowsException() {
        SubscriptionRequest request = SubscriptionRequest.builder()
                .planId(1L)
                .tierId(1L)
                .build();

        when(userRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionRepository.findActiveSubscriptionWithLock(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.of(testSubscription));

        assertThrows(BusinessException.class, () -> subscriptionService.subscribe(1L, request));
        verify(subscriptionRepository, never()).save(any(UserSubscription.class));
    }

    @Test
    void subscribe_UserNotFound_ThrowsException() {
        SubscriptionRequest request = SubscriptionRequest.builder()
                .planId(1L)
                .tierId(1L)
                .build();

        when(userRepository.findByIdWithLock(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.subscribe(1L, request));
    }

    @Test
    void getCurrentSubscription_Success() {
        when(subscriptionRepository.findActiveSubscription(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.of(testSubscription));

        assertDoesNotThrow(() -> subscriptionService.getCurrentSubscription(1L));

        verify(subscriptionRepository, times(1)).findActiveSubscription(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void getCurrentSubscription_NoActiveSubscription_ThrowsException() {
        when(subscriptionRepository.findActiveSubscription(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.getCurrentSubscription(1L));
    }

    @Test
    void updateOrderStats_Success() {
        OrderUpdateRequest request = OrderUpdateRequest.builder()
                .orderValue(new BigDecimal("100.00"))
                .build();

        when(subscriptionRepository.findActiveSubscriptionWithLock(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.of(testSubscription));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(tierService.findEligibleTier(any(), any(), any())).thenReturn(testTier);
        when(subscriptionRepository.save(any(UserSubscription.class))).thenReturn(testSubscription);

        assertDoesNotThrow(() -> subscriptionService.updateOrderStats(1L, request));

        verify(subscriptionRepository, times(1)).save(any(UserSubscription.class));
    }

    @Test
    void cancelSubscription_Success() {
        when(subscriptionRepository.findActiveSubscriptionWithLock(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.of(testSubscription));

        subscriptionService.cancelSubscription(1L);

        verify(subscriptionRepository, times(1)).save(any(UserSubscription.class));
        assertEquals(SubscriptionStatus.CANCELLED, testSubscription.getStatus());
    }
}
