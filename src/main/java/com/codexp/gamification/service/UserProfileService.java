package com.codexp.gamification.service;

import com.codexp.gamification.domain.event.UserCreatedEvent;
import com.codexp.gamification.domain.model.UserScore;
import com.codexp.gamification.domain.repository.UserScoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserProfileService {

    private final UserScoreRepository userScoreRepository;

    public UserProfileService(UserScoreRepository userScoreRepository) {
        this.userScoreRepository = userScoreRepository;
    }

    @Transactional
    public void initializeProfile(UserCreatedEvent event) {
        if (userScoreRepository.existsById(event.userId())) {
            return;
        }

        UserScore profile = UserScore.builder()
                .userId(event.userId())
                .totalPoints(0)
                .level(1)
                .xp(0)
                .updatedAt(LocalDateTime.now())
                .build();

        userScoreRepository.save(profile);
    }
}
