package com.codexp.gamification.controller;

import com.codexp.gamification.domain.dto.AchievementResponse;
import com.codexp.gamification.domain.dto.LeaderboardEntryResponse;
import com.codexp.gamification.domain.dto.UserProfileResponse;
import com.codexp.gamification.domain.enums.AnalysisStatus;
import com.codexp.gamification.domain.event.AnalysisReadyData;
import com.codexp.gamification.domain.event.AnalysisReadyEvent;
import com.codexp.gamification.service.AchievementService;
import com.codexp.gamification.service.LeaderboardService;
import com.codexp.gamification.service.ScoreProcessorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/gamification")
public class GamificationController {

    private final LeaderboardService leaderboardService;
    private final AchievementService achievementService;
    private final ScoreProcessorService scoreProcessorService;

    public GamificationController(
            LeaderboardService leaderboardService,
            AchievementService achievementService,
            ScoreProcessorService scoreProcessorService
    ) {
        this.leaderboardService = leaderboardService;
        this.achievementService = achievementService;
        this.scoreProcessorService = scoreProcessorService;
    }

    @GetMapping("/leaderboard/global")
    public List<LeaderboardEntryResponse> getGlobalLeaderboard() {
        return leaderboardService.getGlobalLeaderboard();
    }

    @GetMapping("/leaderboard/challenge/{id}")
    public List<LeaderboardEntryResponse> getChallengeLeaderboard(@PathVariable UUID id) {
        return leaderboardService.getChallengeLeaderboard(id);
    }

    @GetMapping("/users/{userId}/profile")
    public UserProfileResponse getUserProfile(@PathVariable UUID userId) {
        return achievementService.getUserProfile(userId);
    }

    @GetMapping("/achievements")
    public List<AchievementResponse> getAchievements() {
        return achievementService.getAllAchievements();
    }

    @GetMapping("/test/process")
    public String processTestEvent() {
        AnalysisReadyEvent event = new AnalysisReadyEvent(
                UUID.randomUUID().toString(),
                "AnalysisReadyEvent",
                new AnalysisReadyData(
                        UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                        UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                        UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"),
                        AnalysisStatus.PASSED,
                        85,
                        120L
                )
        );

        scoreProcessorService.processAnalysisReady(event);
        return "Evento procesado";
    }
}