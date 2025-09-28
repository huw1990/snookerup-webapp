package com.snookerup.controllers;

import com.snookerup.services.ScoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import static org.mockito.Mockito.*;

/**
 * Unit tests for the ScoreRestController class.
 *
 * @author Huw
 */
class ScoreRestControllerTests {

    private static final String USERNAME = "joe.bloggs";

    private ScoreService mockScoreService;
    private OidcUser mockOidcUser;

    ScoreRestController scoreRestController;

    @BeforeEach
    public void beforeEach() {
        mockScoreService = mock(ScoreService.class);
        mockOidcUser = mock(OidcUser.class);

        scoreRestController = new ScoreRestController(mockScoreService);

        when(mockOidcUser.getName()).thenReturn(USERNAME);
    }

    @Test
    public void deleteScoreById_Should_InvokeScoreService() {
        // Define variables
        Long scoreId = 10L;

        // Set mock expectations

        // Execute method under test
        scoreRestController.deleteScoreById(scoreId, mockOidcUser);

        // Verify
        verify(mockScoreService).deleteScoreForIdAndPlayerUsername(scoreId, USERNAME);
    }
}
