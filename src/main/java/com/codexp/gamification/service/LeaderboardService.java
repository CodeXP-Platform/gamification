package com.codexp.gamification.service;

import com.codexp.gamification.domain.dto.LeaderboardEntryResponse;
import com.codexp.gamification.domain.enums.LeaderboardCategory;
import com.codexp.gamification.domain.model.LeaderboardEntry;
import com.codexp.gamification.domain.model.UserScore;
import com.codexp.gamification.domain.repository.LeaderboardEntryRepository;
import com.codexp.gamification.domain.repository.PointHistoryRepository;
import com.codexp.gamification.domain.repository.UserScoreRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LeaderboardService {

    private final UserScoreRepository userScoreRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final LeaderboardEntryRepository leaderboardEntryRepository;
    private final UsernameResolver usernameResolver;

    public LeaderboardService(
            UserScoreRepository userScoreRepository,
            PointHistoryRepository pointHistoryRepository,
            LeaderboardEntryRepository leaderboardEntryRepository,
            UsernameResolver usernameResolver
    ) {
        this.userScoreRepository = userScoreRepository;
        this.pointHistoryRepository = pointHistoryRepository;
        this.leaderboardEntryRepository = leaderboardEntryRepository;
        this.usernameResolver = usernameResolver;
    }

    @Transactional
    public void rebuildGlobalLeaderboard() {
        List<UserScore> topUsers = userScoreRepository.findTopUsers(PageRequest.of(0, 100));

        leaderboardEntryRepository.deleteByCategory(LeaderboardCategory.GLOBAL);

        List<LeaderboardEntry> entries = new ArrayList<>();
        int rank = 1;

        for (UserScore userScore : topUsers) {
            entries.add(
                    LeaderboardEntry.builder()
                            .rankPosition(rank++)
                            .userId(userScore.getUserId())
                            .username(usernameResolver.resolve(userScore.getUserId()))
                            .score(userScore.getTotalPoints())
                            .category(LeaderboardCategory.GLOBAL)
                            .targetId(null)
                            .updatedAt(LocalDateTime.now())
                            .build()
            );
        }

        leaderboardEntryRepository.saveAll(entries);
    }

    @Transactional
    public void rebuildChallengeLeaderboard(UUID challengeId) {
        List<Object[]> rows = pointHistoryRepository.calculateChallengeLeaderboard(challengeId);

        leaderboardEntryRepository.deleteByCategoryAndTargetId(
                LeaderboardCategory.CHALLENGE_SPECIFIC,
                challengeId
        );

        List<LeaderboardEntry> entries = new ArrayList<>();
        int rank = 1;

        for (Object[] row : rows) {
            UUID userId = (UUID) row[0];
            Number scoreValue = (Number) row[1];
            int score = scoreValue != null ? scoreValue.intValue() : 0;

            entries.add(
                    LeaderboardEntry.builder()
                            .rankPosition(rank++)
                            .userId(userId)
                            .username(usernameResolver.resolve(userId))
                            .score(score)
                            .category(LeaderboardCategory.CHALLENGE_SPECIFIC)
                            .targetId(challengeId)
                            .updatedAt(LocalDateTime.now())
                            .build()
            );
        }

        leaderboardEntryRepository.saveAll(entries);
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntryResponse> getGlobalLeaderboard() {
        return leaderboardEntryRepository
                .findTop100ByCategoryOrderByRankPositionAsc(LeaderboardCategory.GLOBAL)
                .stream()
                .map(entry -> new LeaderboardEntryResponse(
                        entry.getRankPosition(),
                        entry.getUserId(),
                        entry.getUsername(),
                        entry.getScore()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntryResponse> getChallengeLeaderboard(UUID challengeId) {
        return leaderboardEntryRepository
                .findByCategoryAndTargetIdOrderByRankPositionAsc(
                        LeaderboardCategory.CHALLENGE_SPECIFIC,
                        challengeId
                )
                .stream()
                .map(entry -> new LeaderboardEntryResponse(
                        entry.getRankPosition(),
                        entry.getUserId(),
                        entry.getUsername(),
                        entry.getScore()
                ))
                .toList();
    }
}