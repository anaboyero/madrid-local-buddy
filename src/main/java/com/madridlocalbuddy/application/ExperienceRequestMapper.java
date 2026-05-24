package com.madridlocalbuddy.application;

import com.madridlocalbuddy.api.ExperienceRequestPayload;
import com.madridlocalbuddy.domain.ExperienceCatalog;
import com.madridlocalbuddy.domain.ExperienceRequest;
import com.madridlocalbuddy.domain.Visitor;

public class ExperienceRequestMapper {

    public ExperienceRequest toDomain(ExperienceRequestPayload payload, ExperienceCatalog catalog) {
        var experience = catalog.findById(payload.experienceId()).orElseThrow();
        var visitor = new Visitor(payload.visitorEmail(), payload.nativeEnglishSpeaker());
        return new ExperienceRequest(experience, visitor, payload.comment());
    }
}
