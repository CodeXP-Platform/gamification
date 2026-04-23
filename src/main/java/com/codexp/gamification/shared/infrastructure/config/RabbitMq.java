package com.codexp.gamification.shared.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMq {

    @Bean
    public TopicExchange codeAnalysisExchange(RabbitAppProperties properties) {
        return new TopicExchange(properties.getExchanges().getCodeAnalysis());
    }

    @Bean
    public TopicExchange gamificationExchange(RabbitAppProperties properties) {
        return new TopicExchange(properties.getExchanges().getGamification());
    }

    @Bean
    public Queue analysisReadyQueue(RabbitAppProperties properties) {
        return new Queue(properties.getQueues().getAnalysisReady(), true);
    }

    @Bean
    public Binding analysisReadyBinding(
            Queue analysisReadyQueue,
            TopicExchange codeAnalysisExchange,
            RabbitAppProperties properties
    ) {
        return BindingBuilder
                .bind(analysisReadyQueue)
                .to(codeAnalysisExchange)
                .with(properties.getRoutingKeys().getAnalysisReady());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}