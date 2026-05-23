package com.madridlocalbuddy.api;

import com.madridlocalbuddy.config.AppConfig;
import com.madridlocalbuddy.support.ContractCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExperiencesController.class)
@Import(AppConfig.class)
class ExperiencesControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getExperiences_returns200WithTwoExperiences() throws Exception {
        mockMvc.perform(get("/api/experiences"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(ContractCatalog.CINEMA_ID))
                .andExpect(jsonPath("$[0].title").value(ContractCatalog.CINEMA_TITLE))
                .andExpect(jsonPath("$[0].description").value(ContractCatalog.CINEMA_DESCRIPTION))
                .andExpect(jsonPath("$[1].id").value(ContractCatalog.CASA_DE_CAMPO_WALK_ID))
                .andExpect(jsonPath("$[1].title").value(ContractCatalog.CASA_DE_CAMPO_WALK_TITLE))
                .andExpect(jsonPath("$[1].description").value(ContractCatalog.CASA_DE_CAMPO_WALK_DESCRIPTION));
    }

    @Test
    void postExperiences_returns405() throws Exception {
        mockMvc.perform(post("/api/experiences"))
                .andExpect(status().isMethodNotAllowed());
    }
}
