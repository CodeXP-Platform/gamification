package com.codexp.gamification.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record AchievementUnlockedData(
        UUID userId,
        UUID achievementId,
        String achievementName,
        LocalDateTime unlockedAt
) {
}