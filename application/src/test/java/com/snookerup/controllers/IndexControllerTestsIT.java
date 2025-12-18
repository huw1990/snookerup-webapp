package com.snookerup.controllers;

import com.snookerup.BaseTestcontainersIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Integration tests for the IndexController class.
 *
 * @author Huw
 */
@ActiveProfiles("dev")
@SpringBootTest
@AutoConfigureMockMvc
class IndexControllerTestsIT extends BaseTestcontainersIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getIndex_When_NotLoggedIn() throws Exception {
        this.mockMvc
                .perform(get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getIndex_When_LoggedIn() throws Exception {
        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
        this.mockMvc
                .perform(get("/")
                        .with(oidcLogin().oidcUser(user)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
