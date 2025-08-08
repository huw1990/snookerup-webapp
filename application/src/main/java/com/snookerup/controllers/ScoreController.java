package com.snookerup.controllers;

import com.snookerup.errorhandling.InvalidScoreException;
import com.snookerup.model.*;
import com.snookerup.model.db.Score;
import com.snookerup.services.RoutineService;
import com.snookerup.services.ScoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Controller serving all routes related to scores.
 *
 * @author Huw
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class ScoreController {

    /** The value provided as a routine ID when looking to search across all routines, not a specific one. */
    public static final String DEFAULT_ROUTINE_ID = "all";

    /** A String format for the redirect when mandatory score params are missing. */
    protected static final String VIEW_SCORES_DEFAULT_REDIRECT =
            "redirect:/scores?routineId=%1$s&pageNumber=%2$s&from=%3$s&to=%4$s";

    /** Redirect to use when handling a new score submitted by the user. */
    protected static final String ADD_SCORE_REDIRECT = "redirect:/addscore?routineId=";

    /** Error message to display in a banner when unable to save a user's score. */
    protected static final String UNABLE_TO_SAVE_SCORE_ERROR_MESSAGE =
            "Oops! Some entries weren't valid, please try again.";

    /** Success message to display in a banner when a user's score is saved to the DB. */
    protected static final String SUCCESSFUL_SAVE_SCORE_MESSAGE = "Great job! Your score was added successfully.";

    /** Score service, used for adding and retrieving scores. */
    private final ScoreService scoreService;

    /** Routine service, used for getting information about routines. */
    private final RoutineService routineService;

    /**
     * Gets scores for a user, with filters applied based on request params. Note that of all parameters, routineId,
     * pageNumber, from, and to are required, and force a redirect with default values (of routine ID "all", page number
     * 1, and the previous week as a date range).
     * @param model The model, to add context
     * @param routineId Optional routine ID to search for. If not included, defaults to "all".
     * @param pageNumber Optional page number. If not included, defaults to 1.
     * @param from Optional from date. If not included, defaults to 1 week ago.
     * @param to Optional to date. If not included, defaults to now.
     * @param loop Optional param to search for scores with loop enabled.
     * @param cushionLimit Optional param to search for scores with set cushion limit.
     * @param unitNumber Optional param to search for scores with set unit number.
     * @param potInOrder Optional param to search for scores with pot in order enabled.
     * @param stayOnOneSideOfTable Optional param to search for scores with stay on one side of the table enabled.
     * @param ballStriking Optional param to search for scores with set ball striking.
     * @param user The current logged-in user
     * @return The view to load
     */
    @GetMapping("/scores")
    public String getScores(Model model,
                            @RequestParam Optional<String> routineId,
                            @RequestParam Optional<Integer> pageNumber,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> from,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> to,
                            @RequestParam Optional<Boolean> loop,
                            @RequestParam Optional<Integer> cushionLimit,
                            @RequestParam Optional<Integer> unitNumber,
                            @RequestParam Optional<Boolean> potInOrder,
                            @RequestParam Optional<Boolean> stayOnOneSideOfTable,
                            @RequestParam Optional<String> ballStriking,
                            @AuthenticationPrincipal OidcUser user) {
        Optional<Routine> routineFromId = Optional.empty();
        if (routineId.isPresent()) {
            routineFromId = routineService.getRoutineById(routineId.get());
        };
        if (routineId.isEmpty() || (!DEFAULT_ROUTINE_ID.equals(routineId.get()) && routineFromId.isEmpty())
                || pageNumber.isEmpty() || from.isEmpty() || to.isEmpty()) {
            log.debug("Getting scores with no params, redirecting with default params");
            // Get the required params as entered, or defaults if not provided
            String routineIdForRedirect = routineId.orElse(DEFAULT_ROUTINE_ID);
            Integer pageNumberForRedirect = pageNumber.orElse(1);
            LocalDateTime fromDateTimeForRedirect = from.orElse(LocalDateTime.now().minusWeeks(1).truncatedTo(ChronoUnit.MINUTES));
            LocalDateTime toDateTimeForRedirect = to.orElse(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
            String redirect = String.format(VIEW_SCORES_DEFAULT_REDIRECT,
                    routineIdForRedirect, pageNumberForRedirect, fromDateTimeForRedirect, toDateTimeForRedirect);
            log.debug("Redirecting to {}", redirect);
            return redirect;
        }
        routineFromId.ifPresent((routine) -> {
            log.debug("selectedRoutineId={}", routine.getId());
            model.addAttribute("selectedRoutineId", routine.getId());
            model.addAttribute("selectedRoutine", routine);
        });
        model.addAttribute("routines", routineService.getAllRoutines());
        ScorePageRequestParams params = new ScorePageRequestParams(user.getName(), routineId.get(),
                pageNumber.orElse(1), from.get(), to.get(), loop.orElse(null), cushionLimit.orElse(null),
                unitNumber.orElse(null), potInOrder.orElse(null), stayOnOneSideOfTable.orElse(null),
                ballStriking.orElse(null));
        ScorePage scorePage = scoreService.getScorePageForParams(params);
        model.addAttribute("pageOfScores", scorePage);
        return "scores";
    }

    /**
     * Get the page to allow the user to add a new score to the DB.
     * @param model The model, to add context
     * @param routineId The requested routine ID, if provided
     * @param user The current logged-in user
     * @return The view to load
     */
    @GetMapping("/addscore")
    public String getAddNewScore(Model model,
                                 @RequestParam Optional<String> routineId,
                                 @AuthenticationPrincipal OidcUser user) {
        log.debug("getAddNewScore routineId = {}", routineId);
        Score score = new Score();
        routineId.ifPresent((id) -> {
            Optional<Routine> routineOpt = routineService.getRoutineById(id);
            routineOpt.ifPresent(routine -> {
                log.debug("selectedRoutineId={}", routine.getId());
                score.setRoutineId(id);
                model.addAttribute("selectedRoutineId", id);
                model.addAttribute("selectedRoutine", routine);
            });
        });
        score.setPlayerUsername(user.getName());
        model.addAttribute("routines", routineService.getAllRoutines());
        model.addAttribute("score", score);
        return "addscore";
    }

    /**
     * Handles a user submitting a new score to the DB.
     * @param scoreToBeAdded The score to add.
     * @param bindingResult The binding result, containing details of whether the provided score passed validation.
     * @param user The logged-in user making the request
     * @param model The model, to provide context about the page we will return.
     * @param redirectAttributes Redirect attributes, used for flash messages
     * @return The view to load after the score submission operation is processed
     */
    @PostMapping("/addscore")
    public String addScore(
            @Valid Score scoreToBeAdded,
            BindingResult bindingResult,
            @AuthenticationPrincipal OidcUser user,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        log.debug("scoreToBeAdded={}", scoreToBeAdded);
        if (bindingResult.hasErrors()) {
            /*
             * Score is invalid since it has binding errors - don't re render with those errors, since the score input
             * form is just a series of checkboxes and input fields with validation (so in theory all binding errors
             * would be the result of users tampering with the form inputs in client code), so instead just display an
             * error banner and get the user to re-enter their details.
             */
            log.debug("Have binding errors (bindingResult={}), displaying error message", bindingResult);
            redirectAttributes.addFlashAttribute("message", UNABLE_TO_SAVE_SCORE_ERROR_MESSAGE);
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return ADD_SCORE_REDIRECT + scoreToBeAdded.getRoutineId();
        } else {
            boolean addedScoreSuccessfully = false;
            if (user.getName().equals(scoreToBeAdded.getPlayerUsername())) {
                if (scoreToBeAdded.getNote() != null && scoreToBeAdded.getNote().isBlank()) {
                    scoreToBeAdded.setNote(null);
                }
                try {
                    Score savedScore = scoreService.saveNewScore(scoreToBeAdded);
                    addedScoreSuccessfully = true;
                    log.debug("Score added to DB successfully, score={}", savedScore);
                    redirectAttributes.addFlashAttribute("message", SUCCESSFUL_SAVE_SCORE_MESSAGE);
                    redirectAttributes.addFlashAttribute("messageType", "success");
                } catch (InvalidScoreException ex) {
                    log.debug("Invalid score, was not added to DB, ex=", ex);
                }
            } else {
                log.debug("Player username on score to add ({}) doesn't match logged in user ({}), so not adding to DB",
                        scoreToBeAdded.getPlayerUsername(), user.getName());
            }
            if (!addedScoreSuccessfully) {
                log.debug("Couldn't add score to DB, displaying error message");
                redirectAttributes.addFlashAttribute("message", UNABLE_TO_SAVE_SCORE_ERROR_MESSAGE);
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return ADD_SCORE_REDIRECT + scoreToBeAdded.getRoutineId();
            }
        }

        // Always redirect back to the add score page, regardless of success or failure
        return ADD_SCORE_REDIRECT + scoreToBeAdded.getRoutineId();
    }

    /**
     * Handles the AJAX request to load the input fields for only those variations that apply to the provided routine.
     * @param model The model, to add context for the view we're going to load
     * @param routineId The routine ID the score is being submitted against.
     * @return The view to load
     */
    @GetMapping("/addscore-variations-frag")
    public String getVariationsForRoutineFragment(Model model, @RequestParam String routineId) {
        log.debug("getVariationsForRoutineFragment routineId={}", routineId);
        Optional<Routine> routineOpt = routineService.getRoutineById(routineId);
        routineOpt.ifPresent(routine -> {
            model.addAttribute("selectedRoutineId", routineId);
            model.addAttribute("selectedRoutine", routine);
        });
        return "fragments/addScoreVariations :: scoreVariations";
    }
}
