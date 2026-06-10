package com.codexp.gamification.shared.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rabbitmq")
public class RabbitAppProperties {

    private Exchanges exchanges = new Exchanges();
    private Queues queues = new Queues();
    private RoutingKeys routingKeys = new RoutingKeys();

    public Exchanges getExchanges() {
        return exchanges;
    }

    public void setExchanges(Exchanges exchanges) {
        this.exchanges = exchanges;
    }

    public Queues getQueues() {
        return queues;
    }

    public void setQueues(Queues queues) {
        this.queues = queues;
    }

    public RoutingKeys getRoutingKeys() {
        return routingKeys;
    }

    public void setRoutingKeys(RoutingKeys routingKeys) {
        this.routingKeys = routingKeys;
    }

    public static class Exchanges {
        private String codeAnalysis;
        private String gamification;
        private String iam;

        public String getCodeAnalysis() {
            return codeAnalysis;
        }

        public void setCodeAnalysis(String codeAnalysis) {
            this.codeAnalysis = codeAnalysis;
        }

        public String getGamification() {
            return gamification;
        }

        public void setGamification(String gamification) {
            this.gamification = gamification;
        }

        public String getIam() {
            return iam;
        }

        public void setIam(String iam) {
            this.iam = iam;
        }
    }

    public static class Queues {
        private String analysisReady;
        private String userCreated;

        public String getAnalysisReady() {
            return analysisReady;
        }

        public void setAnalysisReady(String analysisReady) {
            this.analysisReady = analysisReady;
        }

        public String getUserCreated() {
            return userCreated;
        }

        public void setUserCreated(String userCreated) {
            this.userCreated = userCreated;
        }
    }

    public static class RoutingKeys {
        private String analysisReady;
        private String achievementUnlocked;
        private String userCreated;

        public String getAnalysisReady() {
            return analysisReady;
        }

        public void setAnalysisReady(String analysisReady) {
            this.analysisReady = analysisReady;
        }

        public String getAchievementUnlocked() {
            return achievementUnlocked;
        }

        public void setAchievementUnlocked(String achievementUnlocked) {
            this.achievementUnlocked = achievementUnlocked;
        }

        public String getUserCreated() {
            return userCreated;
        }

        public void setUserCreated(String userCreated) {
            this.userCreated = userCreated;
        }
    }
}