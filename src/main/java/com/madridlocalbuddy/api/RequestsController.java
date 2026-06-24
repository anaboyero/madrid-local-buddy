package com.madridlocalbuddy.api;

import com.madridlocalbuddy.application.ExperienceRequestMapper;
import com.madridlocalbuddy.application.HostNotificationException;
import com.madridlocalbuddy.application.HostNotifier;
import com.madridlocalbuddy.application.RequestRepository;
import com.madridlocalbuddy.domain.ExperienceCatalog;
import com.madridlocalbuddy.domain.ExperienceRequestValidator;
import com.madridlocalbuddy.domain.StoredExperienceRequest;
import com.madridlocalbuddy.domain.ValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final ExperienceRequestMapper mapper;
    private final RequestRepository requestRepository;
    private final HostNotifier hostNotifier;

    public RequestsController(
            ExperienceCatalog catalog,
            ExperienceRequestValidator validator,
            ExperienceRequestMapper mapper,
            RequestRepository requestRepository,
            HostNotifier hostNotifier) {
        this.catalog = catalog;
        this.validator = validator;
        this.mapper = mapper;
        this.requestRepository = requestRepository;
        this.hostNotifier = hostNotifier;
    }

    @GetMapping
    public List<StoredRequestResponse> listRequests() {
        return requestRepository.findAllNewestFirst().stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    public ResponseEntity<?> createRequest(@RequestBody ExperienceRequestPayload payload) {
        List<ValidationError> errors = validator.validate(payload, catalog);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(new RequestResponse.Failure(false, errors));
        }

        var domainRequest = mapper.toDomain(payload, catalog);
        long id = requestRepository.save(domainRequest);

        try {
            hostNotifier.notify(domainRequest);
        } catch (HostNotificationException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new RequestResponse.ServiceUnavailable(false, "Unable to notify host"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new RequestResponse.Success(true, id));
    }

    private StoredRequestResponse toResponse(StoredExperienceRequest stored) {
        String experienceTitle = catalog.findById(stored.experienceId())
                .map(experience -> experience.title())
                .orElse("");
        return new StoredRequestResponse(
                stored.id(),
                stored.experienceId(),
                experienceTitle,
                stored.visitorEmail(),
                stored.comment(),
                stored.nativeEnglishSpeaker(),
                stored.createdAt());
    }
}
