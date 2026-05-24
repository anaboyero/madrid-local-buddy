package com.madridlocalbuddy.domain;

import com.madridlocalbuddy.api.ExperienceRequestPayload;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ExperienceRequestValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public List<ValidationError> validate(ExperienceRequestPayload payload, ExperienceCatalog catalog) {
        List<ValidationError> errors = new ArrayList<>();

        if (catalog.findById(payload.experienceId()).isEmpty()) {
            errors.add(new ValidationError("experienceId", "Unknown experience"));
        }

        if (payload.visitorEmail() == null || !EMAIL_PATTERN.matcher(payload.visitorEmail()).matches()) {
            errors.add(new ValidationError("visitorEmail", "Invalid email address"));
        }

        if (payload.comment() == null || payload.comment().isBlank()) {
            errors.add(new ValidationError("comment", "Preferred date or time is required"));
        }

        return errors;
    }
}
