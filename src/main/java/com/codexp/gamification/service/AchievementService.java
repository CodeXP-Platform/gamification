package com.codexp.gamification.service;

import com.codexp.gamification.domain.dto.AchievementResponse;
import com.codexp.gamification.domain.dto.UserAchievementResponse;
import com.codexp.gamification.domain.dto.UserProfileResponse;
import com.codexp.gamification.domain.event.AchievementUnlockedData;
import com.codexp.gamification.domain.event.AchievementUnlockedEvent;
import com.codexp.gamification.domain.event.AnalysisReadyData;
import com.codexp.gamification.domain.model.Achievement;
import com.codexp.gamification.domain.model.UserAchievement;
import com.codexp.gamification.domain.model.UserScore;
import com.codexp.gamification.domain.repository.AchievementRepository;
import com.codexp.gamification.domain.repository.PointHistoryRepository;
import com.codexp.gamification.domain.repository.UserAchievementRepository;
import com.codexp.gamification.domain.repository.UserScoreRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final UserScoreRepository userScoreRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final ObjectMapper objectMapper;

    public AchievementService(
            AchievementRepository achievementRepository,
            UserAchievementRepository userAchievementRepository,
            UserScoreRepository userScoreRepository,
            PointHistoryRepository pointHistoryRepository,
            ObjectMapper objectMapper
    ) {
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
        this.userScoreRepository = userScoreRepository;
        this.pointHistoryRepository = pointHistoryRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public List<AchievementResponse> getAllAchievements() {
        return achievementRepository.findAll()
                .stream()
                .map(achievement -> new AchievementResponse(
                        achievement.getId(),
                        achievement.getName(),
                        achievement.getDescription(),
                        achievement.getCriteria(),
                        achievement.getBadgeImageUrl()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(UUID userId) {
        UserScore userScore = userScoreRepository.findById(userId)
                .orElse(
                        UserScore.builder()
                                .userId(userId)
                                .totalPoints(0)
                                .level(1)
                                .xp(0)
                                .updatedAt(LocalDateTime.now())
                                .build()
                );

        List<UserAchievementResponse> achievements = userAchievementRepository
                .findByUserIdOrderByUnlockedAtDesc(userId)
                .stream()
                .map(userAchievement -> new UserAchievementResponse(
                        userAchievement.getAchievement().getId(),
                        userAchievement.getAchievement().getName(),
                        userAchievement.getAchievement().getDescription(),
                        userAchievement.getAchievement().getBadgeImageUrl(),
                        userAchievement.getUnlockedAt()
                ))
                .toList();

        return new UserProfileResponse(
                userId,
                userScore.getTotalPoints(),
                userScore.getLevel(),
                userScore.getXp(),
                achievements
        );
    }

    @Transactional
    public List<AchievementUnlockedEvent> evaluateAchievements(
            UUID userId,
            AnalysisReadyData data,
            UserScore currentScore
    ) {
        long solvedChallenges = pointHistoryRepository.countDistinctSolvedChallenges(userId);
        List<Achievement> achievements = achievementRepository.findAll();
        List<AchievementUnlockedEvent> unlockedEvents = new ArrayList<>();

        for (Achievement achievement : achievements) {
            boolean alreadyUnlocked = userAchievementRepository
                    .existsByUserIdAndAchievement_Id(userId, achievement.getId());

            if (alreadyUnlocked) {
                continue;
            }

            if (matchesCriteria(achievement, data, currentScore, solvedChallenges)) {
                UserAchievement userAchievement = UserAchievement.builder()
                        .userId(userId)
                        .achievement(achievement)
                        .unlockedAt(LocalDateTime.now())
                        .build();

                userAchievementRepository.save(userAchievement);

                unlockedEvents.add(
                        new AchievementUnlockedEvent(
                                UUID.randomUUID().toString(),
                                "AchievementUnlockedEvent",
                                new AchievementUnlockedData(
                                        userId,
                                        achievement.getId(),
                                        achievement.getName(),
                                        userAchievement.getUnlockedAt()
                                )
                        )
                );
            }
        }

        return unlockedEvents;
    }

    private boolean matchesCriteria(
            Achievement achievement,
            AnalysisReadyData data,
            UserScore currentScore,
            long solvedChallenges
    ) {
        try {
            JsonNode criteria = objectMapper.readTree(achievement.getCriteria());

            if (criteria.has("minSolvedChallenges")
                    && solvedChallenges < criteria.get("minSolvedChallenges").asInt()) {
                return false;
            }

            if (criteria.has("minAiScore")) {
                if (data.aiScore() == null || data.aiScore() < criteria.get("minAiScore").asInt()) {
                    return false;
                }
            }

            if (criteria.has("maxExecutionTimeMs")) {
                if (data.executionTimeMs() == null
                        || data.executionTimeMs() > criteria.get("maxExecutionTimeMs").asLong()) {
                    return false;
                }
            }

            if (criteria.has("minLevel")
                    && currentScore.getLevel() < criteria.get("minLevel").asInt()) {
                return false;
            }

            if (criteria.has("challengeId")) {
                String requiredChallengeId = criteria.get("challengeId").asText();
                if (data.challengeId() == null || !data.challengeId().toString().equals(requiredChallengeId)) {
                    return false;
                }
            }

            return true;
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "No se pudo evaluar el achievement: " + achievement.getName(),
                    ex
            );
        }
    }
}