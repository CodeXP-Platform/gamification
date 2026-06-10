package com.codexp.gamification.domain.repository;

import com.codexp.gamification.domain.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AchievementRepository extends JpaRepository<Achievement, UUID> {
}