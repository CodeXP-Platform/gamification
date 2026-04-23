package com.codexp.gamification.domain.dto;

import java.util.UUID;

public record AchievementResponse(
        UUID id,
        String name,
        String description,
        String criteria,
        String badgeImageUrl
) {
}