package com.codexp.gamification.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsernameResolver {

    public String resolve(UUID userId) {
        return "user-" + userId.toString().substring(0, 8);
    }
}