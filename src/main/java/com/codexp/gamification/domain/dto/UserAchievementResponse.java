package com.codexp.gamification.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserAchievementResponse(
        UUID achievementId,
        String achievementName,
        String description,
        String badgeImageUrl,
        LocalDateTime unlockedAt
) {
}