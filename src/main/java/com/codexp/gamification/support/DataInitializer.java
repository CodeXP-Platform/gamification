package com.codexp.gamification.support;

import com.codexp.gamification.domain.model.Achievement;
import com.codexp.gamification.domain.repository.AchievementRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AchievementRepository achievementRepository;

    public DataInitializer(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    @Override
    public void run(String... args) {
        if (achievementRepository.count() > 0) {
            return;
        }

        List<Achievement> achievements = List.of(
                Achievement.builder()
                        .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                        .name("First Blood")
                        .description("Resolvió su primer reto correctamente.")
                        .criteria("{\"minSolvedChallenges\":1}")
                        .badgeImageUrl("https://cdn.codexp.com/badges/first-blood.png")
                        .build(),
                Achievement.builder()
                        .id(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                        .name("Code Master")
                        .description("Obtuvo un AI score de 90 o más.")
                        .criteria("{\"minAiScore\":90}")
                        .badgeImageUrl("https://cdn.codexp.com/badges/code-master.png")
                        .build(),
                Achievement.builder()
                        .id(UUID.fromString("33333333-3333-3333-3333-333333333333"))
                        .name("Speed Runner")
                        .description("Resolución con ejecución rápida.")
                        .criteria("{\"maxExecutionTimeMs\":150}")
                        .badgeImageUrl("https://cdn.codexp.com/badges/speed-runner.png")
                        .build(),
                Achievement.builder()
                        .id(UUID.fromString("44444444-4444-4444-4444-444444444444"))
                        .name("Consistent Solver")
                        .description("Completó al menos 5 retos.")
                        .criteria("{\"minSolvedChallenges\":5}")
                        .badgeImageUrl("https://cdn.codexp.com/badges/consistent-solver.png")
                        .build(),
                Achievement.builder()
                        .id(UUID.fromString("55555555-5555-5555-5555-555555555555"))
                        .name("Level Up")
                        .description("Alcanzó el nivel 3.")
                        .criteria("{\"minLevel\":3}")
                        .badgeImageUrl("https://cdn.codexp.com/badges/level-up.png")
                        .build()
        );

        achievementRepository.saveAll(achievements);
    }
}