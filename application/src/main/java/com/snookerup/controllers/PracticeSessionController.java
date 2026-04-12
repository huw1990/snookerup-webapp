package com.snookerup.controllers;

import com.snookerup.errorhandling.NoPracticeSessionSlotsRemainingException;
import com.snookerup.errorhandling.NonUniquePracticeSessionTitleException;
import com.snookerup.model.Routine;
import com.snookerup.model.RoutineAdditionToPracticeSession;
import com.snookerup.model.addedcontext.PracticeSessionWithRoutineContext;
import com.snookerup.model.db.nosql.PracticeSession;
import com.snookerup.services.PracticeSessionService;
import com.snookerup.services.RoutineService;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

/**
 * Controller serving all routes related to practice sessions.
 *
 * @author Huw
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class PracticeSessionController {

    /** Prefix to add to a redirect URL. */
    protected static final String REDIRECT_PREFIX = "redirect:";

    /** A String format for the redirect to go to a newly created practice session. */
    protected static final String VIEW_RECENTLY_CREATED_PRACTICE_SESSION_REDIRECT = "redirect:/practicesessions/%1$s";

    /** Redirect to use when handling an invalid new practice session submitted by the user. */
    protected static final String ADD_PRACTICE_SESSION_REDIRECT = "redirect:/addpracticesession";

    /** Redirect to use when a user tries to add a new practice session but has no slots remaining. */
    protected static final String ALL_PRACTICE_SESSIONS_REDIRECT = "redirect:/practicesessions";

    /** Redirect to use when handling a new addition to a practice session, submitted by the user. */
    protected static final String ADD_TO_PRACTICE_SESSION_REDIRECT =
            "redirect:/addtopracticesession";

    /** URL for the page to add to a practice session. */
    protected static final String ADD_TO_PRACTICE_SESSION_URL = "/addtopracticesession";

    /** Error message to display in a banner when unable to save a user's practice session. */
    protected static final String UNABLE_TO_SAVE_PRACTICE_SESSION_ERROR_MESSAGE =
            "Oops! Some entries weren't valid, please try again.";

    /** Error message to display in a banner when trying to delete a nonexistent practice session. */
    protected static final String NO_PRACTICE_SESSION_TO_DELETE_ERROR_MESSAGE = "No practice session to delete!";

    /** Error message to display in a banner when a user has no remaining slots for new practice sessions. */
    protected static final String NO_PRACTICE_SESSIONS_REMAINING_FOR_PLAYER_ERROR_MESSAGE =
            "Sorry, but you have no practice session slots remaining.";

    /** Error message to display in a banner when a user tries to add a practice session with the same title as another of their sessions. */
    protected static final String EXISTING_PRACTICE_SESSION_WITH_SAME_TITLE_ERROR_MESSAGE =
            "Sorry, but you already have a practice session with the same title. Please try again with a different title.";

    /** Success message to display in a banner when a user's practice session is saved to the DB. */
    protected static final String SUCCESSFUL_SAVE_PRACTICE_SESSION_MESSAGE =
            "Great job! Your practice session was created successfully.";

    /** Success message to display in a banner when a user's practice session addition is saved to the DB. */
    protected static final String SUCCESSFUL_SAVE_PRACTICE_SESSION_ADDITION_MESSAGE =
            "Great job! Your practice session addition was saved successfully.";

    /** Success message to display in a banner when a user's practice session deletion succeeds. */
    protected static final String SUCCESSFUL_PRACTICE_SESSION_DELETE_MESSAGE =
            "Great job! Your practice session was deleted successfully.";

    /** Error message to display in a banner when unable to save a user's practice session additions. */
    protected static final String UNABLE_TO_ADD_ROUTINE_TO_PRACTICE_SESSION_ERROR_MESSAGE =
            "Oops! Some entries weren't valid, please try again.";

    /** Service to get practice sessions from. */
    private final PracticeSessionService practiceSessionService;

    /** Service to get routines from. */
    private final RoutineService routineService;

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
            try {
                PracticeSession savedPracticeSession = practiceSessionService
                        .saveNewPracticeSession(practiceSessionToBeAdded);
                meterRegistry.gauge("snookerup.practicesession.created", 1);
                log.debug("Practice session added to DB successfully, practice session={}", savedPracticeSession);
                redirectAttributes.addFlashAttribute("message", SUCCESSFUL_SAVE_PRACTICE_SESSION_MESSAGE);
                redirectAttributes.addFlashAttribute("messageType", "success");
                return String.format(VIEW_RECENTLY_CREATED_PRACTICE_SESSION_REDIRECT, savedPracticeSession.getId());
            } catch (NoPracticeSessionSlotsRemainingException ex) {
                log.debug("No practice session slots remaining for player username={}", user.getName());
                log.debug("Couldn't add practice session to DB, displaying error message");
                redirectAttributes.addFlashAttribute("message", NO_PRACTICE_SESSIONS_REMAINING_FOR_PLAYER_ERROR_MESSAGE);
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return ALL_PRACTICE_SESSIONS_REDIRECT;
            } catch (NonUniquePracticeSessionTitleException ex) {
                log.debug("Existing practice session found for player ({}) with same title={}", user.getName(),
                        practiceSessionToBeAdded.getTitle());
                log.debug("Couldn't add practice session to DB, displaying error message");
                redirectAttributes.addFlashAttribute("message", EXISTING_PRACTICE_SESSION_WITH_SAME_TITLE_ERROR_MESSAGE);
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return ADD_PRACTICE_SESSION_REDIRECT;
            }

        }
    }

    /**
     * Get the page to allow the user to add a routine to an existing practice session in the DB.
     * @param model The model, to add context
     * @param routineId The requested routine ID to add, if provided
     * @param user The current logged-in user
     * @return The view to load
     */
    @GetMapping("/addtopracticesession")
    public String getAddToPracticeSession(Model model,
                                 @RequestParam Optional<String> routineId,
                                 @RequestParam Optional<String> practiceSessionTitle,
                                 @AuthenticationPrincipal OidcUser user) {
        log.debug("getAddToPracticeSession routineId = {}", routineId);
        RoutineAdditionToPracticeSession practiceSessionAddition = new RoutineAdditionToPracticeSession();
        routineId.ifPresent((id) -> {
            Optional<Routine> routineOpt = routineService.getRoutineById(id);
            routineOpt.ifPresent(routine -> {
                log.debug("selectedRoutineId={}", routine.getId());
                practiceSessionAddition.setRoutineId(id);
                model.addAttribute("selectedRoutineId", id);
                model.addAttribute("selectedRoutine", routine);
            });
        });
        model.addAttribute("routines", routineService.getAllRoutines());
        model.addAttribute("practiceSessionAddition", practiceSessionAddition);
        model.addAttribute("practiceSessions", practiceSessionService
                .getPracticeSessionsForPlayerUsername(user.getName()));
        if (practiceSessionTitle.isPresent()) {
            model.addAttribute("selectedPracticeSessionTitle", practiceSessionTitle.get());
        }
        return "addToPracticeSession";
    }

    /**
     * Handles a user submitting a routine addition to an existing practice session in the DB.
     * @param practiceSessionAddition The addition to the session.
     * @param bindingResult The binding result, containing details of whether the provided routine addition passed validation.
     * @param user The logged-in user making the request
     * @param redirectAttributes Redirect attributes, used for flash messages
     * @return The view to load after the score submission operation is processed
     */
    @PostMapping("/addtopracticesession")
    public String addToPracticeSession(
            @Valid RoutineAdditionToPracticeSession practiceSessionAddition,
            BindingResult bindingResult,
            @AuthenticationPrincipal OidcUser user,
            RedirectAttributes redirectAttributes
    ) {
        log.debug("practiceSessionAddition={}", practiceSessionAddition);
        String practiceSessionTitle = null;
        if (practiceSessionAddition.getPracticeSessionId() != null) {
            PracticeSessionWithRoutineContext practiceSession = practiceSessionService.getPracticeSessionByIdAndPlayerUsername(
                    practiceSessionAddition.getPracticeSessionId(), user.getName());
            if (practiceSession != null) {
                practiceSessionTitle = practiceSession.getTitle();
            }
        }
        if (bindingResult.hasErrors()) {
            /*
             * Session addition is invalid since it has binding errors - don't re render with those errors, since the
             * session addition input form is just a series of checkboxes and input fields with validation (so in
             * theory all binding errors would be the result of users tampering with the form inputs in client code),
             * so instead just display an error banner and get the user to re-enter their details.
             */
            log.debug("Have binding errors (bindingResult={}), displaying error message", bindingResult);
            redirectAttributes.addFlashAttribute("message", UNABLE_TO_ADD_ROUTINE_TO_PRACTICE_SESSION_ERROR_MESSAGE);
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return getAddToPracticeSessionRedirect(Optional.ofNullable(practiceSessionAddition.getRoutineId()),
                    Optional.ofNullable(practiceSessionTitle));
        } else {
            PracticeSession modifiedPracticeSession = practiceSessionService.addRoutineToPracticeSession(
                    practiceSessionAddition, user.getName());
            if (modifiedPracticeSession == null) {
                log.debug("Couldn't add session addition to DB, displaying error message");
                redirectAttributes.addFlashAttribute("message", UNABLE_TO_SAVE_PRACTICE_SESSION_ERROR_MESSAGE);
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return getAddToPracticeSessionRedirect(Optional.ofNullable(practiceSessionAddition.getRoutineId()),
                        Optional.ofNullable(practiceSessionTitle));
            } else {
                log.debug("Successfully saved practice session addition, displaying success message");
                redirectAttributes.addFlashAttribute("message", SUCCESSFUL_SAVE_PRACTICE_SESSION_ADDITION_MESSAGE);
                redirectAttributes.addFlashAttribute("messageType", "success");
                return getAddToPracticeSessionRedirect(Optional.ofNullable(practiceSessionAddition.getRoutineId()),
                        Optional.ofNullable(practiceSessionTitle));
            }
        }
    }

    /**
     * Get the confirmation page for deleting an individual practice session.
     * @param id The practice session ID to delete
     * @param model The Spring MVC model
     * @param user The logged-in user performing the action, i.e. the practice session owner
     * @return The practice session deletion confirmation page to display
     */
    @GetMapping("/practicesessions/{id}/delete")
    public String getPracticeSessionDeleteById(@PathVariable("id") String id, Model model, @AuthenticationPrincipal OidcUser user) {
        model.addAttribute("practiceSession",
                practiceSessionService.getPracticeSessionByIdAndPlayerUsername(id, user.getName()));
        return "deletePracticeSession";
    }

    /**
     * Confirms the deletion of an individual practice session, redirecting to the practice sessions overview page.
     * @param id The practice session ID to delete
     * @param user The logged-in user performing the action, i.e. the practice session owner
     * @param redirectAttributes Redirect attributes, to show a response of the action before the redirect
     * @return A redirect to the practice sessions overview page
     */
    @GetMapping("/practicesessions/{id}/delete/confirm")
    public String getConfirmPracticeSessionDeleteById(@PathVariable("id") String id,
                                                      @AuthenticationPrincipal OidcUser user,
                                                      RedirectAttributes redirectAttributes) {
        PracticeSession deletedPracticeSession = practiceSessionService.deletePracticeSession(id, user.getName());
        if (deletedPracticeSession == null) {
            log.debug("Couldn't delete practice session from DB, displaying error message");
            redirectAttributes.addFlashAttribute("message", NO_PRACTICE_SESSION_TO_DELETE_ERROR_MESSAGE);
            redirectAttributes.addFlashAttribute("messageType", "danger");
        } else {
            log.debug("Successfully deleted practice session addition, displaying success message");
            redirectAttributes.addFlashAttribute("message", SUCCESSFUL_PRACTICE_SESSION_DELETE_MESSAGE);
            redirectAttributes.addFlashAttribute("messageType", "success");
        }
        return ALL_PRACTICE_SESSIONS_REDIRECT;
    }

    /**
     * Constructs a redirect URL for adding to a practice session, with optional query parameters.
     * @param routineId The routine ID
     * @param practiceSessionTitle The practice session title
     * @return A redirect URL, with optional query parameters
     */
    private String getAddToPracticeSessionRedirect(Optional<String> routineId, Optional<String> practiceSessionTitle) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(ADD_TO_PRACTICE_SESSION_URL);
        if (routineId.isPresent()) {
            builder.queryParam("routineId", routineId.get());
        }
        if (practiceSessionTitle.isPresent()) {
            builder.queryParam("practiceSessionTitle", practiceSessionTitle.get());
        }
        return REDIRECT_PREFIX + builder.encode().build().toUriString();
    }
}