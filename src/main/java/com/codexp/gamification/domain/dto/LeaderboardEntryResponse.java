package com.codexp.gamification.domain.dto;

import java.util.UUID;

public record LeaderboardEntryResponse(
        Integer rank,
        UUID userId,
        String username,
        Integer score
) {
}