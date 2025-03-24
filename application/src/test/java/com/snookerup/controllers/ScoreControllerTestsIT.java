//package com.snookerup.controllers;
//
//import com.snookerup.BaseTestcontainersIT;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.oauth2.core.oidc.OidcIdToken;
//import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
//import org.springframework.security.oauth2.core.oidc.user.OidcUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.Instant;
//import java.util.Map;
//
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
///**
// * Integration tests for the ScoreController class.
// *
// * @author Huw
// */
//@ActiveProfiles("dev")
//@SpringBootTest
//@AutoConfigureMockMvc
//class ScoreControllerTestsIT extends BaseTestcontainersIT {
//
//    private static final String LOGIN_REDIRECT_URL = "http://localhost/oauth2/authorization/cognito";
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Test
//    void getAllScores_Should_RedirectToLogin_When_NotAuthed() throws Exception {
//        this.mockMvc
//                .perform(get("/scores"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl(LOGIN_REDIRECT_URL));
//    }
//
//    @Test
//    void getAllScores_Should_Return200OK_When_CorrectlyAuthed() throws Exception {
//        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
//        this.mockMvc
//                .perform(get("/scores")
//                        .with(oidcLogin().oidcUser(user)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void getAddNewScore_Should_RedirectToLogin_When_NotAuthed() throws Exception {
//        this.mockMvc
//                .perform(get("/addscore"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl(LOGIN_REDIRECT_URL));
//    }
//
//    @Test
//    void getAddNewScore_Should_Return200OK_When_CorrectlyAuthed() throws Exception {
//        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
//        this.mockMvc
//                .perform(get("/addscore")
//                        .with(oidcLogin().oidcUser(user)))
//                .andExpect(status().isOk());
//    }
//
//    private DefaultOidcUser createOidcUser(String email, String username) {
//        return new DefaultOidcUser(
//                null,
//                new OidcIdToken(
//                        "some-id",
//                        Instant.now(),
//                        Instant.MAX,
//                        Map.of(
//                                "email", email,
//                                "sub", "snookerup",
//                                "name", username
//                        )
//                )
//        );
//    }
//}
