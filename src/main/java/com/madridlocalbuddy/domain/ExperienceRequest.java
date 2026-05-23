package com.madridlocalbuddy.domain;

public record ExperienceRequest(
        int experienceId,
        String visitorEmail,
        String comment,
        boolean nativeEnglishSpeaker) {
}
