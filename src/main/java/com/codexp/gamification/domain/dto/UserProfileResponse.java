package com.codexp.gamification.domain.dto;

import java.util.List;
import java.util.UUID;

public record UserProfileResponse(
        UUID userId,
        Integer totalPoints,
        Integer level,
        Integer xp,
        List<UserAchievementResponse> achievements
) {
}