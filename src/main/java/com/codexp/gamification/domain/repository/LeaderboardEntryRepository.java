package com.codexp.gamification.domain.repository;

import com.codexp.gamification.domain.enums.LeaderboardCategory;
import com.codexp.gamification.domain.model.LeaderboardEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LeaderboardEntryRepository extends JpaRepository<LeaderboardEntry, UUID> {

    List<LeaderboardEntry> findTop100ByCategoryOrderByRankPositionAsc(LeaderboardCategory category);

    List<LeaderboardEntry> findByCategoryAndTargetIdOrderByRankPositionAsc(
            LeaderboardCategory category,
            UUID targetId
    );

    void deleteByCategory(LeaderboardCategory category);

    void deleteByCategoryAndTargetId(LeaderboardCategory category, UUID targetId);
}