package com.codexp.gamification.shared.infrastructure.messaging;

import com.codexp.gamification.domain.event.UserCreatedEvent;
import com.codexp.gamification.service.UserProfileService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UserCreatedEventListener {

    private final UserProfileService userProfileService;

    public UserCreatedEventListener(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @RabbitListener(queues = "${app.rabbitmq.queues.user-created}")
    public void onUserCreated(UserCreatedEvent event) {
        userProfileService.initializeProfile(event);
    }
}
