package com.codexp.gamification.domain.repository;

import com.codexp.gamification.domain.enums.PointReason;
import com.codexp.gamification.domain.model.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PointHistoryRepository extends JpaRepository<PointHistory, UUID> {

    long countByUserIdAndReason(UUID userId, PointReason reason);

    boolean existsByUserIdAndReasonAndCreatedAtBetween(
            UUID userId,
            PointReason reason,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""
        select count(distinct ph.challengeId)
        from PointHistory ph
        where ph.userId = :userId and ph.reason = com.codexp.gamification.domain.enums.PointReason.CHALLENGE_SOLVED
    """)
    long countDistinctSolvedChallenges(@Param("userId") UUID userId);

    @Query("""
        select ph.userId, sum(ph.pointsEarned)
        from PointHistory ph
        where ph.challengeId = :challengeId
        group by ph.userId
        order by sum(ph.pointsEarned) desc
    """)
    List<Object[]> calculateChallengeLeaderboard(@Param("challengeId") UUID challengeId);
}