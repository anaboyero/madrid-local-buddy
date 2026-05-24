package com.madridlocalbuddy.api;

public record ExperienceRequestPayload(
        int experienceId,
        String visitorEmail,
        String comment,
        boolean nativeEnglishSpeaker) {
}
