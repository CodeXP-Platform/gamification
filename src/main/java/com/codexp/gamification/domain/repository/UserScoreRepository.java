package com.codexp.gamification.domain.repository;

import com.codexp.gamification.domain.model.UserScore;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface UserScoreRepository extends JpaRepository<UserScore, UUID> {

    @Query("select us from UserScore us order by us.totalPoints desc, us.xp desc, us.updatedAt asc")
    List<UserScore> findTopUsers(Pageable pageable);
}