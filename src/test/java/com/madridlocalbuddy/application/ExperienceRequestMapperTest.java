package com.madridlocalbuddy.application;

import com.madridlocalbuddy.domain.ExperienceCatalog;
import com.madridlocalbuddy.support.ContractCatalog;
import com.madridlocalbuddy.support.ContractRequests;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExperienceRequestMapperTest {

    private final ExperienceRequestMapper mapper = new ExperienceRequestMapper();
    private final ExperienceCatalog catalog = new ExperienceCatalog();

    @Test
    void toDomain_mapsPayloadToEnrichedExperienceRequest() {
        var domain = mapper.toDomain(ContractRequests.VALID_PAYLOAD, catalog);

        assertEquals(ContractCatalog.CINEMA, domain.experience());
        assertEquals("visitor@example.com", domain.visitor().email());
        assertEquals(true, domain.visitor().nativeEnglishSpeaker());
        assertEquals("Saturday afternoon would work best for me", domain.comment());
    }
}
