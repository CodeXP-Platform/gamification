package com.codexp.gamification.service;

import com.codexp.gamification.domain.enums.AnalysisStatus;
import com.codexp.gamification.domain.enums.PointReason;
import com.codexp.gamification.domain.event.AchievementUnlockedEvent;
import com.codexp.gamification.domain.event.AnalysisReadyData;
import com.codexp.gamification.domain.event.AnalysisReadyEvent;
import com.codexp.gamification.domain.model.PointHistory;
import com.codexp.gamification.domain.model.UserScore;
import com.codexp.gamification.domain.repository.PointHistoryRepository;
import com.codexp.gamification.domain.repository.UserScoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ScoreProcessorService {

    private final UserScoreRepository userScoreRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final LeaderboardService leaderboardService;
    private final AchievementService achievementService;

    public ScoreProcessorService(
            UserScoreRepository userScoreRepository,
            PointHistoryRepository pointHistoryRepository,
            LeaderboardService leaderboardService,
            AchievementService achievementService
    ) {
        this.userScoreRepository = userScoreRepository;
        this.pointHistoryRepository = pointHistoryRepository;
        this.leaderboardService = leaderboardService;
        this.achievementService = achievementService;
    }

    @Transactional
    public List<AchievementUnlockedEvent> processAnalysisReady(AnalysisReadyEvent event) {
        if (event == null || event.data() == null) {
            return List.of();
        }

        AnalysisReadyData data = event.data();

        if (data.status() != AnalysisStatus.PASSED) {
            return List.of();
        }

        UUID userId = data.userId();
        UUID challengeId = data.challengeId();

        long solvedChallengesBefore = pointHistoryRepository.countByUserIdAndReason(
                userId,
                PointReason.CHALLENGE_SOLVED
        );

        boolean solvedYesterday = solvedYesterday(userId);
        boolean alreadyHasTodayStreakBonus = alreadyHasTodayStreakBonus(userId);

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

        int basePoints = calculateBasePoints(data.aiScore(), data.executionTimeMs());
        applyPoints(userScore, challengeId, basePoints, PointReason.CHALLENGE_SOLVED);

        if (solvedChallengesBefore == 0) {
            applyPoints(userScore, challengeId, 50, PointReason.FIRST_SUBMISSION);
        }

        if (solvedYesterday && !alreadyHasTodayStreakBonus) {
            applyPoints(userScore, challengeId, 20, PointReason.STREAK_BONUS);
        }

        userScoreRepository.save(userScore);

        leaderboardService.rebuildGlobalLeaderboard();
        leaderboardService.rebuildChallengeLeaderboard(challengeId);

        return achievementService.evaluateAchievements(userId, data, userScore);
    }

    private int calculateBasePoints(Integer aiScore, Long executionTimeMs) {
        int safeAiScore = aiScore != null ? aiScore : 0;
        long safeExecutionTime = executionTimeMs != null ? executionTimeMs : Long.MAX_VALUE;

        int points = safeAiScore;

        if (safeExecutionTime <= 100) {
            points += 25;
        } else if (safeExecutionTime <= 200) {
            points += 10;
        }

        return Math.max(points, 10);
    }

    private void applyPoints(
            UserScore userScore,
            UUID challengeId,
            int points,
            PointReason reason
    ) {
        userScore.setXp(userScore.getXp() + points);
        userScore.setTotalPoints(userScore.getTotalPoints() + points);
        userScore.setLevel(calculateLevel(userScore.getXp()));
        userScore.setUpdatedAt(LocalDateTime.now());

        PointHistory pointHistory = PointHistory.builder()
                .userId(userScore.getUserId())
                .challengeId(challengeId)
                .pointsEarned(points)
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .build();

        pointHistoryRepository.save(pointHistory);
    }

    private int calculateLevel(int xp) {
        return (xp / 100) + 1;
    }

    private boolean solvedYesterday(UUID userId) {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.plusDays(1).atStartOfDay().minusNanos(1);

        return pointHistoryRepository.existsByUserIdAndReasonAndCreatedAtBetween(
                userId,
                PointReason.CHALLENGE_SOLVED,
                start,
                end
        );
    }

    private boolean alreadyHasTodayStreakBonus(UUID userId) {
        LocalDate today = LocalDate.now();

        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay().minusNanos(1);

        return pointHistoryRepository.existsByUserIdAndReasonAndCreatedAtBetween(
                userId,
                PointReason.STREAK_BONUS,
                start,
                end
        );
    }
}