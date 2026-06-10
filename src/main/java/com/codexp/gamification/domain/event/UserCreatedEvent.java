package com.codexp.gamification.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserCreatedEvent(
        UUID userId,
        String email,
        String nickname,
        String role,
        LocalDateTime occurredAt
) {}
