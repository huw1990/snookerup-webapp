package com.snookerup.controllers;

import com.snookerup.errorhandling.NoPracticeSessionSlotsRemainingException;
import com.snookerup.errorhandling.NonUniquePracticeSessionTitleException;
import com.snookerup.model.db.nosql.PracticeSession;
import com.snookerup.services.PracticeSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

/**
 * A Spring REST controller providing REST endpoints related to practice sessions, typically made as extra in-browser
 * requests rather than as part of loading a new page with Thymeleaf.
 *
 * @author Huw
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class PracticeSessionRestController {

    private final PracticeSessionService sessionService;

    @PostMapping("/practicesessions")
    @ResponseStatus(HttpStatus.CREATED)
    public PracticeSession createPracticeSession(@RequestBody @Valid PracticeSession practiceSession,
                                                                @AuthenticationPrincipal OidcUser user)
            throws NoPracticeSessionSlotsRemainingException, NonUniquePracticeSessionTitleException {
        log.debug("createPracticeSession practiceSession={}", practiceSession);

        // Sanitize the practiceSession
        practiceSession.setId(null);
        practiceSession.getRoutines().clear();
        practiceSession.setPlayerUsername(user.getName());

        // Save the practice session into the DB
        try {
            PracticeSession createdPracticeSession = sessionService.saveNewPracticeSession(practiceSession);
            log.debug("Created practice session for user={}", user.getName());
            return createdPracticeSession;
        } catch (NoPracticeSessionSlotsRemainingException | NonUniquePracticeSessionTitleException ex) {
            log.error("Exception creating practiceSession={}", ex.getMessage());
            throw ex;
        }
    }

}