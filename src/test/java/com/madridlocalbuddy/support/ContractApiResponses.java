package com.madridlocalbuddy.support;

import com.madridlocalbuddy.domain.ValidationError;

import java.time.Instant;
import java.util.List;

public final class ContractApiResponses {

    public record Created(boolean ok, long id) {}

    public record Failure(boolean ok, List<ValidationError> errors) {}

    public record ServiceUnavailable(boolean ok, String message) {}

    public record StoredRequest(
            long id,
            int experienceId,
            String experienceTitle,
            String visitorEmail,
            String comment,
            boolean nativeEnglishSpeaker,
            Instant createdAt) {}

    public static final Created CREATED_CINEMA = new Created(true, 1);

    public static final Failure INVALID_EMAIL_FAILURE =
            new Failure(false, List.of(new ValidationError("visitorEmail", "Invalid email address")));

    public static final Failure EMPTY_COMMENT_FAILURE =
            new Failure(false, List.of(new ValidationError("comment", "Preferred date or time is required")));

    public static final Failure UNKNOWN_EXPERIENCE_FAILURE =
            new Failure(false, List.of(new ValidationError("experienceId", "Unknown experience")));

    public static final ServiceUnavailable NOTIFICATION_FAILED =
            new ServiceUnavailable(false, "Unable to notify host");

    public static StoredRequest cinemaStored(long id) {
        return new StoredRequest(
                id,
                ContractCatalog.CINEMA_ID,
                ContractCatalog.CINEMA_TITLE,
                "visitor@example.com",
                "Saturday afternoon would work best for me",
                true,
                null);
    }

    public static StoredRequest casaDeCampoStored(long id) {
        return new StoredRequest(
                id,
                ContractCatalog.CASA_DE_CAMPO_WALK_ID,
                ContractCatalog.CASA_DE_CAMPO_WALK_TITLE,
                "other@example.com",
                "Sunday morning works for me",
                false,
                null);
    }

    private ContractApiResponses() {}
}
