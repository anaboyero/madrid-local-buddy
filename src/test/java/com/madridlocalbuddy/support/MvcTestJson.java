package com.madridlocalbuddy.support;

import com.madridlocalbuddy.domain.ValidationError;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class MvcTestJson {

    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    private MvcTestJson() {
    }

    public static <T> T readBody(MvcResult result, Class<T> type) throws Exception {
        return MAPPER.readValue(result.getResponse().getContentAsString(), type);
    }

    public static List<ContractApiResponses.StoredRequest> readStoredRequestList(MvcResult result)
            throws Exception {
        return MAPPER.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {});
    }

    public static void assertStoredRequestsEqual(
            List<ContractApiResponses.StoredRequest> actual,
            List<ContractApiResponses.StoredRequest> expected) {
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(expected);
        actual.forEach(item -> assertThat(item.createdAt()).isNotNull());
    }
}
