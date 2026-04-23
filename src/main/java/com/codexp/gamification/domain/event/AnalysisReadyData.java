package com.codexp.gamification.domain.event;

import com.codexp.gamification.domain.enums.AnalysisStatus;

import java.util.UUID;

public record AnalysisReadyData(
        UUID solutionId,
        UUID userId,
        UUID challengeId,
        AnalysisStatus status,
        Integer aiScore,
        Long executionTimeMs
) {
}