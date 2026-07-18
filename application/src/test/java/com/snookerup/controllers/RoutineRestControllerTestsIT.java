package com.snookerup.controllers;

import com.snookerup.BaseTestcontainersIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the RoutineRestController class.
 *
 * @author Huw
 */
@ActiveProfiles("dev")
@SpringBootTest
@AutoConfigureMockMvc
class RoutineRestControllerTestsIT extends BaseTestcontainersIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRoutineOverviews_Should_Return200OK_When_NoAuth() throws Exception {
        String searchTerm = "line";
        int page = 0;
        int size = 18;
        this.mockMvc
                .perform(get("/routine-overviews")
                        .queryParam("search", searchTerm)
                        .queryParam("page", String.valueOf(page))
                        .queryParam("size", String.valueOf(size))
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
