package com.snookerup.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snookerup.BaseTestcontainersIT;
import com.snookerup.model.Registration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Integration tests for the RegistrationController class.
 *
 * @author Huw
 */
@ActiveProfiles("dev")
@SpringBootTest
@AutoConfigureMockMvc
class RegistrationControllerTestsIT extends BaseTestcontainersIT {

    private static final String USERNAME = "username";
    private static final String EMAIL = "test@example.com";
    private static final String VALID_INVITATION_CODE = "CRUCIBLE";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRegisterView() throws Exception {
        this.mockMvc
                .perform(get("/register"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void registerUser() throws Exception {
        Registration validRegistration = new Registration();
        validRegistration.setUsername(USERNAME);
        validRegistration.setEmail(EMAIL);
        validRegistration.setInvitationCode(VALID_INVITATION_CODE);

        this.mockMvc
                .perform(post("/register")
                        .with(csrf())
                        .content(new ObjectMapper().writeValueAsString(validRegistration)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
