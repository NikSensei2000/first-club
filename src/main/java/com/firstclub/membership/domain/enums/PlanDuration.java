package com.firstclub.membership.domain.enums;

public enum PlanDuration {
    MONTHLY(1),
    QUARTERLY(3),
    YEARLY(12);

    private final int months;

    PlanDuration(int months) {
        this.months = months;
    }

    public int getMonths() {
        return months;
    }
}
