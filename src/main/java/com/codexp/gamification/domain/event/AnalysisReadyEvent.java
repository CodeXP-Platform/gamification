package com.codexp.gamification.domain.event;

public record AnalysisReadyEvent(
        String eventId,
        String eventType,
        AnalysisReadyData data
) {
}