package com.madridlocalbuddy.infrastructure;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "experience_requests")
class ExperienceRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int experienceId;

    private String visitorEmail;

    private String comment;

    private boolean nativeEnglishSpeaker;

    private Instant createdAt;

    Long getId() {
        return id;
    }

    int getExperienceId() {
        return experienceId;
    }

    void setExperienceId(int experienceId) {
        this.experienceId = experienceId;
    }

    String getVisitorEmail() {
        return visitorEmail;
    }

    void setVisitorEmail(String visitorEmail) {
        this.visitorEmail = visitorEmail;
    }

    String getComment() {
        return comment;
    }

    void setComment(String comment) {
        this.comment = comment;
    }

    boolean isNativeEnglishSpeaker() {
        return nativeEnglishSpeaker;
    }

    void setNativeEnglishSpeaker(boolean nativeEnglishSpeaker) {
        this.nativeEnglishSpeaker = nativeEnglishSpeaker;
    }

    Instant getCreatedAt() {
        return createdAt;
    }

    void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
