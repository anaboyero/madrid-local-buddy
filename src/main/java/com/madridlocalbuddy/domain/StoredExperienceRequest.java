package com.madridlocalbuddy.domain;

import java.time.Instant;

public record StoredExperienceRequest(
        long id,
        int experienceId,
        String visitorEmail,
        String comment,
        boolean nativeEnglishSpeaker,
        Instant createdAt) {}
