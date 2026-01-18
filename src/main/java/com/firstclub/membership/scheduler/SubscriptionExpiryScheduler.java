package com.firstclub.membership.scheduler;

import com.firstclub.membership.domain.entity.UserSubscription;
import com.firstclub.membership.domain.enums.SubscriptionStatus;
import com.firstclub.membership.repository.UserSubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SubscriptionExpiryScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionExpiryScheduler.class);

    private final UserSubscriptionRepository subscriptionRepository;

    public SubscriptionExpiryScheduler(UserSubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void expireSubscriptions() {
        logger.info("Starting scheduled subscription expiry check");
        LocalDateTime now = LocalDateTime.now();
        logger.debug("Current time: {}, checking for subscriptions expiring before this time", now);

        List<UserSubscription> expiredSubscriptions = subscriptionRepository
                .findExpiredSubscriptions(SubscriptionStatus.ACTIVE, now);

        logger.info("Found {} subscriptions to expire", expiredSubscriptions.size());

        if (expiredSubscriptions.isEmpty()) {
            logger.debug("No subscriptions to expire at this time");
            return;
        }

        int successCount = 0;
        int failureCount = 0;

        for (UserSubscription subscription : expiredSubscriptions) {
            try {
                logger.debug("Expiring subscription - id: {}, userId: {}, expiryDate: {}", 
                           subscription.getId(), subscription.getUserId(), subscription.getExpiryDate());
                
                subscription.setStatus(SubscriptionStatus.EXPIRED);
                subscriptionRepository.save(subscription);
                successCount++;
                
                logger.info("Expired subscription successfully - subscriptionId: {}, userId: {}", 
                           subscription.getId(), subscription.getUserId());
            } catch (Exception e) {
                failureCount++;
                logger.error("Failed to expire subscription - subscriptionId: {}, userId: {}, error: {}", 
                           subscription.getId(), subscription.getUserId(), e.getMessage(), e);
            }
        }

        logger.info("Subscription expiry check completed - Total: {}, Success: {}, Failed: {}", 
                   expiredSubscriptions.size(), successCount, failureCount);
    }
}
