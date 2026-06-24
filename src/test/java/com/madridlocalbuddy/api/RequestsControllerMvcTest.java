package com.madridlocalbuddy.api;

import com.madridlocalbuddy.application.HostNotificationException;
import com.madridlocalbuddy.application.HostNotifier;
import com.madridlocalbuddy.domain.ExperienceRequest;
import com.madridlocalbuddy.support.ContractApiResponses;
import com.madridlocalbuddy.support.ContractRequests;
import com.madridlocalbuddy.support.MvcTestJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RequestsControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private HostNotifier hostNotifier;

    @BeforeEach
    void setUp() {
        reset(hostNotifier);
        jdbcTemplate.execute("DELETE FROM experience_requests");
        jdbcTemplate.execute("ALTER TABLE experience_requests ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    void postRequests_withValidPayload_returns201WithId() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ContractRequests.VALID_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(MvcTestJson.readBody(result, ContractApiResponses.Created.class))
                .isEqualTo(ContractApiResponses.CREATED_CINEMA);

        verify(hostNotifier).notify(any(ExperienceRequest.class));
    }

    @Test
    void postRequests_withValidPayload_notifiesHost() throws Exception {
        mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ContractRequests.VALID_JSON))
                .andExpect(status().isCreated());

        verify(hostNotifier).notify(any(ExperienceRequest.class));
    }

    @Test
    void getRequests_afterPost_returnsStoredRequest() throws Exception {
        mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ContractRequests.VALID_JSON))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(get("/api/requests"))
                .andExpect(status().isOk())
                .andReturn();

        MvcTestJson.assertStoredRequestsEqual(
                MvcTestJson.readStoredRequestList(result), List.of(ContractApiResponses.cinemaStored(1)));
    }

    @Test
    void postRequests_withInvalidPayload_doesNotPersist() throws Exception {
        String body =
                """
                {
                  "experienceId": 1,
                  "visitorEmail": "not-an-email",
                  "comment": "Saturday afternoon would work best for me",
                  "nativeEnglishSpeaker": true
                }
                """;

        MvcResult postResult = mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(MvcTestJson.readBody(postResult, ContractApiResponses.Failure.class))
                .isEqualTo(ContractApiResponses.INVALID_EMAIL_FAILURE);

        MvcResult getResult = mockMvc.perform(get("/api/requests"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(MvcTestJson.readStoredRequestList(getResult)).isEmpty();
    }

    @Test
    void postRequests_whenNotificationFails_stillPersistsRequest() throws Exception {
        doThrow(new HostNotificationException("Unable to notify host"))
                .when(hostNotifier)
                .notify(any(ExperienceRequest.class));

        MvcResult postResult = mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ContractRequests.VALID_JSON))
                .andExpect(status().isServiceUnavailable())
                .andReturn();

        assertThat(MvcTestJson.readBody(postResult, ContractApiResponses.ServiceUnavailable.class))
                .isEqualTo(ContractApiResponses.NOTIFICATION_FAILED);

        MvcResult getResult = mockMvc.perform(get("/api/requests"))
                .andExpect(status().isOk())
                .andReturn();

        MvcTestJson.assertStoredRequestsEqual(
                MvcTestJson.readStoredRequestList(getResult), List.of(ContractApiResponses.cinemaStored(1)));
    }

    @Test
    void getRequests_returnsNewestFirst() throws Exception {
        mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ContractRequests.VALID_JSON))
                .andExpect(status().isCreated());

        String secondRequest =
                """
                {
                  "experienceId": 2,
                  "visitorEmail": "other@example.com",
                  "comment": "Sunday morning works for me",
                  "nativeEnglishSpeaker": false
                }
                """;

        mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondRequest))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(get("/api/requests"))
                .andExpect(status().isOk())
                .andReturn();

        MvcTestJson.assertStoredRequestsEqual(
                MvcTestJson.readStoredRequestList(result),
                List.of(ContractApiResponses.casaDeCampoStored(2), ContractApiResponses.cinemaStored(1)));
    }

    @Test
    void getRequests_whenEmpty_returnsEmptyArray() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/requests"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(MvcTestJson.readStoredRequestList(result)).isEmpty();
    }

    @Test
    void putRequests_returns405() throws Exception {
        mockMvc.perform(put("/api/requests")).andExpect(status().isMethodNotAllowed());
    }

    @Test
    void postRequests_withEmptyComment_returns400WithErrors() throws Exception {
        String body =
                """
                {
                  "experienceId": 1,
                  "visitorEmail": "visitor@example.com",
                  "comment": "",
                  "nativeEnglishSpeaker": true
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(MvcTestJson.readBody(result, ContractApiResponses.Failure.class))
                .isEqualTo(ContractApiResponses.EMPTY_COMMENT_FAILURE);
    }

    @Test
    void postRequests_withUnknownExperienceId_returns400WithErrors() throws Exception {
        String body =
                """
                {
                  "experienceId": 999,
                  "visitorEmail": "visitor@example.com",
                  "comment": "Saturday afternoon would work best for me",
                  "nativeEnglishSpeaker": true
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(MvcTestJson.readBody(result, ContractApiResponses.Failure.class))
                .isEqualTo(ContractApiResponses.UNKNOWN_EXPERIENCE_FAILURE);
    }
}
