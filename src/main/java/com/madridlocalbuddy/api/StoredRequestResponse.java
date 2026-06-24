package com.madridlocalbuddy.api;

import java.time.Instant;

record StoredRequestResponse(
        long id,
        int experienceId,
        String experienceTitle,
        String visitorEmail,
        String comment,
        boolean nativeEnglishSpeaker,
        Instant createdAt) {}
