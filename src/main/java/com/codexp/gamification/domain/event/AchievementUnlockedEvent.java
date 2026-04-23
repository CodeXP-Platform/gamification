package com.codexp.gamification.domain.event;

public record AchievementUnlockedEvent(
        String eventId,
        String eventType,
        AchievementUnlockedData data
) {
}