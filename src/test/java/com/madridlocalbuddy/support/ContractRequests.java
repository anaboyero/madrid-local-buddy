package com.madridlocalbuddy.support;

import com.madridlocalbuddy.api.ExperienceRequestPayload;
import com.madridlocalbuddy.domain.ExperienceRequest;
import com.madridlocalbuddy.domain.Visitor;

public final class ContractRequests {

    public static final String VALID_JSON =
            """
            {
              "experienceId": 1,
              "visitorEmail": "visitor@example.com",
              "comment": "Saturday afternoon would work best for me",
              "nativeEnglishSpeaker": true
            }
            """;

    public static final ExperienceRequestPayload VALID_PAYLOAD = new ExperienceRequestPayload(
            ContractCatalog.CINEMA_ID,
            "visitor@example.com",
            "Saturday afternoon would work best for me",
            true);

    public static final ExperienceRequest VALID_DOMAIN = new ExperienceRequest(
            ContractCatalog.CINEMA,
            new Visitor("visitor@example.com", true),
            "Saturday afternoon would work best for me");

    private ContractRequests() {
    }
}
