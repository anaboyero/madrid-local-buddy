package com.madridlocalbuddy.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ExperienceRequestValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public List<ValidationError> validate(ExperienceRequest request, ExperienceCatalog catalog) {
        List<ValidationError> errors = new ArrayList<>();

        if (catalog.findById(request.experienceId()).isEmpty()) {
            errors.add(new ValidationError("experienceId", "Unknown experience"));
        }

        if (request.visitorEmail() == null || !EMAIL_PATTERN.matcher(request.visitorEmail()).matches()) {
            errors.add(new ValidationError("visitorEmail", "Invalid email address"));
        }

        if (request.comment() == null || request.comment().isBlank()) {
            errors.add(new ValidationError("comment", "Preferred date or time is required"));
        }

        return errors;
    }
}
