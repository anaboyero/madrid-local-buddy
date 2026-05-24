package com.madridlocalbuddy.api;

import com.madridlocalbuddy.domain.ExperienceCatalog;
import com.madridlocalbuddy.domain.ExperienceRequestValidator;
import com.madridlocalbuddy.domain.ValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class RequestsController {

    private final ExperienceCatalog catalog;
    private final ExperienceRequestValidator validator;

    public RequestsController(ExperienceCatalog catalog, ExperienceRequestValidator validator) {
        this.catalog = catalog;
        this.validator = validator;
    }

    @PostMapping
    public ResponseEntity<?> createRequest(@RequestBody ExperienceRequestPayload payload) {
        List<ValidationError> errors = validator.validate(payload, catalog);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(new RequestResponse.Failure(false, errors));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new RequestResponse.Success(true));
    }
}
