package com.snookerup.controllers;

import com.snookerup.model.db.nosql.PracticeSession;
import com.snookerup.services.PracticeSessionService;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller serving all routes related to practice sessions.
 *
 * @author Huw
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class PracticeSessionController {

    /** A String format for the redirect to go to a newly created practice session. */
    protected static final String VIEW_RECENTLY_CREATED_PRACTICE_SESSION_REDIRECT = "redirect:/practicesessions/%1$s";

    /** Redirect to use when handling an invalid new practice session submitted by the user. */
    protected static final String ADD_PRACTICE_SESSION_REDIRECT = "redirect:/addpracticesession";

    /** Error message to display in a banner when unable to save a user's practice session. */
    protected static final String UNABLE_TO_SAVE_PRACTICE_SESSION_ERROR_MESSAGE =
            "Oops! Some entries weren't valid, please try again.";

    /** Success message to display in a banner when a user's practice session is saved to the DB. */
    protected static final String SUCCESSFUL_SAVE_PRACTICE_SESSION_MESSAGE =
            "Great job! Your practice session was created successfully.";

    /** Service to get practice sessions from. */
    private final PracticeSessionService practiceSessionService;

    /** Micrometer registry for tracking metrics. */
    private final MeterRegistry meterRegistry;

    /**
     * Get the practice session overview page, showing the available slots for the user, and any practice sessions
     * filling these slots.
     * @param model The Spring MVC model
     * @param user The logged-in user to get practice sessions for
     * @return The practice sessions overview page to display
     */
    @GetMapping("/practicesessions")
    public String getAllPracticeSessions(Model model, @AuthenticationPrincipal OidcUser user) {
        model.addAttribute("practiceSessions",
                practiceSessionService.getPracticeSessionsForPlayerUsername(user.getName()));
        return "practiceSessions";
    }

    /**
     * Get an individual practice session page, showing details of the practice session.
     * @param id The practice session ID to display
     * @param model The Spring MVC model
     * @param user The logged-in user to get the practice session for, i.e. the practice session owner
     * @return The practice session page to display
     */
    @GetMapping("/practicesessions/{id}")
    public String getPracticeSessionById(@PathVariable("id") String id, Model model, @AuthenticationPrincipal OidcUser user) {
        model.addAttribute("practiceSession",
                practiceSessionService.getPracticeSessionByIdAndPlayerUsername(id, user.getName()));
        return "practiceSession";
    }

    /**
     * Get the page to add a new practice session.
     * @param model The Spring MVC model
     * @param user The logged-in user to add the practice session for
     * @return The add practice session page to display
     */
    @GetMapping("/addpracticesession")
    public String getAddNewPracticeSession(Model model, @AuthenticationPrincipal OidcUser user) {
        PracticeSession practiceSession = new PracticeSession();
        practiceSession.setPlayerUsername(user.getName());
        model.addAttribute("practiceSession", practiceSession);
        return "addPracticeSession";
    }

    /**
     * Handles a user creating a new practice session via form submission.
     * @param practiceSessionToBeAdded The practice session to be created.
     * @param bindingResult The binding result, containing details of whether the provided practice session passed validation.
     * @param user The logged-in user making the request
     * @param model The model, to provide context about the page we will return.
     * @param redirectAttributes Redirect attributes, used for flash messages
     * @return The view to load after the pracice session submission operation is processed
     */
    @PostMapping("/addpracticesession")
    public String addPracticeSession(
            @Valid PracticeSession practiceSessionToBeAdded,
            BindingResult bindingResult,
            Model model,
            @AuthenticationPrincipal OidcUser user,
            RedirectAttributes redirectAttributes
    ) {
        log.debug("practiceSessionToBeAdded={}", practiceSessionToBeAdded);
        if (bindingResult.hasErrors()) {
            log.debug("Have binding errors (bindingResult={}), displaying error message", bindingResult);
            redirectAttributes.addFlashAttribute("message", UNABLE_TO_SAVE_PRACTICE_SESSION_ERROR_MESSAGE);
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return ADD_PRACTICE_SESSION_REDIRECT;
        } else if (!user.getName().equals(practiceSessionToBeAdded.getPlayerUsername())) {
            log.debug("Player username on practice session to add ({}) doesn't match logged in user ({}), so not adding to DB",
                    practiceSessionToBeAdded.getPlayerUsername(), user.getName());
            log.debug("Couldn't add practice session to DB, displaying error message");
            redirectAttributes.addFlashAttribute("message", UNABLE_TO_SAVE_PRACTICE_SESSION_ERROR_MESSAGE);
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return ADD_PRACTICE_SESSION_REDIRECT;
        } else {
            PracticeSession savedPracticeSession = practiceSessionService.saveNewPracticeSession(practiceSessionToBeAdded);
            meterRegistry.gauge("snookerup.practicesession.created", 1);
            log.debug("Practice session added to DB successfully, practice session={}", savedPracticeSession);
            redirectAttributes.addFlashAttribute("message", SUCCESSFUL_SAVE_PRACTICE_SESSION_MESSAGE);
            redirectAttributes.addFlashAttribute("messageType", "success");
            return String.format(VIEW_RECENTLY_CREATED_PRACTICE_SESSION_REDIRECT, savedPracticeSession.getId());
        }
    }
}