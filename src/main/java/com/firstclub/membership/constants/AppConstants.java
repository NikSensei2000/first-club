package com.firstclub.membership.constants;

public final class AppConstants {

    private AppConstants() {
    }

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    
    public static final int JWT_TOKEN_START_INDEX = 7;
    
    public static final String CACHE_MEMBERSHIP_PLANS = "membershipPlans";
    public static final String CACHE_MEMBERSHIP_TIERS = "membershipTiers";
    public static final String CACHE_USER_SUBSCRIPTIONS = "userSubscriptions";
    
    public static final String SUCCESS_MESSAGE_REGISTRATION = "User registered successfully";
    public static final String SUCCESS_MESSAGE_LOGIN = "Login successful";
    public static final String SUCCESS_MESSAGE_SUBSCRIPTION_CREATED = "Subscription created successfully";
    public static final String SUCCESS_MESSAGE_TIER_CHANGED = "Tier changed successfully";
    public static final String SUCCESS_MESSAGE_SUBSCRIPTION_CANCELLED = "Subscription cancelled successfully";
    public static final String SUCCESS_MESSAGE_ORDER_UPDATED = "Order statistics updated successfully";
    
    public static final String ERROR_MESSAGE_USERNAME_EXISTS = "Username already exists";
    public static final String ERROR_MESSAGE_EMAIL_EXISTS = "Email already exists";
    public static final String ERROR_MESSAGE_ACTIVE_SUBSCRIPTION_EXISTS = "User already has an active subscription";
    public static final String ERROR_MESSAGE_SAME_TIER = "User is already on this tier";
    public static final String ERROR_MESSAGE_NO_ACTIVE_SUBSCRIPTION = "No active subscription found for user";
    public static final String ERROR_MESSAGE_INVALID_CREDENTIALS = "Invalid username or password";
}
