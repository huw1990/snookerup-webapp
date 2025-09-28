package com.snookerup.controllers;

import com.snookerup.services.ScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

/**
 * A Spring REST controller providing REST endpoints related to scores, typically made as extra in-browser requests
 * rather than as part of loading a new page with Thymeleaf.
 *
 * @author Huw
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class ScoreRestController {

    private final ScoreService scoreService;

    @DeleteMapping("/scores/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteScoreById(@PathVariable(name = "id") Long scoreId, @AuthenticationPrincipal OidcUser user) {
        log.debug("deleteScoreById scoreId={}", scoreId);

        // Delete by score and player username to ensure scores can only be deleted by the player that made them
        scoreService.deleteScoreForIdAndPlayerUsername(scoreId, user.getName());
    }
}
