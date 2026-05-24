package com.madridlocalbuddy.domain;

import com.madridlocalbuddy.api.ExperienceRequestPayload;
import com.madridlocalbuddy.support.ContractCatalog;
import com.madridlocalbuddy.support.ContractRequests;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExperienceRequestValidatorTest {

    private final ExperienceRequestValidator validator = new ExperienceRequestValidator();
    private final ExperienceCatalog catalog = new ExperienceCatalog();

    @Test
    void validate_returnsNoErrorsForValidPayload() {
        List<ValidationError> errors = validator.validate(ContractRequests.VALID_PAYLOAD, catalog);

        assertTrue(errors.isEmpty());
    }

    @Test
    void validate_returnsErrorForInvalidVisitorEmail() {
        ExperienceRequestPayload payload = new ExperienceRequestPayload(
                ContractCatalog.CINEMA_ID, "not-an-email", "Saturday afternoon", true);

        List<ValidationError> errors = validator.validate(payload, catalog);

        assertEquals(1, errors.size());
        assertEquals("visitorEmail", errors.get(0).field());
        assertEquals("Invalid email address", errors.get(0).message());
    }

    @Test
    void validate_returnsErrorForUnknownExperienceId() {
        ExperienceRequestPayload payload =
                new ExperienceRequestPayload(999, "visitor@example.com", "Saturday afternoon", true);

        List<ValidationError> errors = validator.validate(payload, catalog);

        assertEquals(1, errors.size());
        assertEquals("experienceId", errors.get(0).field());
        assertEquals("Unknown experience", errors.get(0).message());
    }

    @Test
    void validate_returnsErrorForEmptyComment() {
        ExperienceRequestPayload payload =
                new ExperienceRequestPayload(ContractCatalog.CINEMA_ID, "visitor@example.com", "", true);

        List<ValidationError> errors = validator.validate(payload, catalog);

        assertEquals(1, errors.size());
        assertEquals("comment", errors.get(0).field());
        assertEquals("Preferred date or time is required", errors.get(0).message());
    }

    @Test
    void validate_returnsErrorForBlankComment() {
        ExperienceRequestPayload payload =
                new ExperienceRequestPayload(ContractCatalog.CINEMA_ID, "visitor@example.com", "   ", true);

        List<ValidationError> errors = validator.validate(payload, catalog);

        assertEquals(1, errors.size());
        assertEquals("comment", errors.get(0).field());
        assertEquals("Preferred date or time is required", errors.get(0).message());
    }

    @Test
    void validate_returnsErrorForNullComment() {
        ExperienceRequestPayload payload =
                new ExperienceRequestPayload(ContractCatalog.CINEMA_ID, "visitor@example.com", null, true);

        List<ValidationError> errors = validator.validate(payload, catalog);

        assertEquals(1, errors.size());
        assertEquals("comment", errors.get(0).field());
        assertEquals("Preferred date or time is required", errors.get(0).message());
    }
}
