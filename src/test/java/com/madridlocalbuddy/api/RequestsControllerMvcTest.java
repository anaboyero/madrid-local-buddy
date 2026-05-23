package com.madridlocalbuddy.api;

import com.madridlocalbuddy.support.ContractRequests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RequestsControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void postRequests_withValidPayload_returns201AndOkTrue() throws Exception {
        mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ContractRequests.VALID_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ok").value(true));
    }

    @Test
    void postRequests_withInvalidVisitorEmail_returns400WithErrors() throws Exception {
        String body =
                """
                {
                  "experienceId": 1,
                  "visitorEmail": "not-an-email",
                  "comment": "Saturday afternoon would work best for me",
                  "nativeEnglishSpeaker": true
                }
                """;

        mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ok").value(false))
                .andExpect(jsonPath("$.errors[0].field").value("visitorEmail"))
                .andExpect(jsonPath("$.errors[0].message").value("Invalid email address"));
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

        mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ok").value(false))
                .andExpect(jsonPath("$.errors[0].field").value("comment"))
                .andExpect(jsonPath("$.errors[0].message").value("Preferred date or time is required"));
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

        mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ok").value(false))
                .andExpect(jsonPath("$.errors[0].field").value("experienceId"))
                .andExpect(jsonPath("$.errors[0].message").value("Unknown experience"));
    }

    @Test
    void getRequests_returns405() throws Exception {
        mockMvc.perform(get("/api/requests")).andExpect(status().isMethodNotAllowed());
    }
}
