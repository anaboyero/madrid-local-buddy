package com.madridlocalbuddy.domain;

public record ExperienceRequest(
        String experienceId,
        String visitorEmail,
        String comment,
        boolean nativeEnglishSpeaker) {
}
