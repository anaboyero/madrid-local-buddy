package com.madridlocalbuddy.support;

import com.madridlocalbuddy.domain.ExperienceRequest;

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

    public static final ExperienceRequest VALID = new ExperienceRequest(
            ContractCatalog.CINEMA_ID,
            "visitor@example.com",
            "Saturday afternoon would work best for me",
            true);

    private ContractRequests() {
    }
}
