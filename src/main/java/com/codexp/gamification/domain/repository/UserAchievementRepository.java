package com.codexp.gamification.domain.repository;

import com.codexp.gamification.domain.model.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, UUID> {

    boolean existsByUserIdAndAchievement_Id(UUID userId, UUID achievementId);

    List<UserAchievement> findByUserIdOrderByUnlockedAtDesc(UUID userId);
}