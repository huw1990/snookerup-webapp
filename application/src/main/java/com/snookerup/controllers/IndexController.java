package com.snookerup.controllers;

import com.snookerup.model.Last30DaysStats;
import com.snookerup.model.db.nosql.Routine;
import com.snookerup.services.RoutineService;
import com.snookerup.services.ScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller serving the templated SnookerUp homepage.
 *
 * @author Huw
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class IndexController {

    /** The number of recent scores to display for logged in users on the dashboard page. */
    protected static final int NUMBER_OF_RECENT_SCORES_TO_DISPLAY = 3;

    private final RoutineService routineService;

    private final ScoreService scoreService;

    @GetMapping("/")
    public String getIndex(Model model, @AuthenticationPrincipal OidcUser user) {
        boolean haveLast30DaysStats = false;
        if (user != null && scoreService.hasPlayerPostedScoreInLast30Days(user.getName())) {
            log.debug("Displaying activity dashboard for user {}", user.getName());
            Last30DaysStats last30DaysStats = scoreService.getLast30DaysStats(user.getName());
            model.addAttribute("last30DaysStats", last30DaysStats);
            haveLast30DaysStats = true;
            model.addAttribute("recentScores", scoreService.getLastXScores(user.getName(),
                    NUMBER_OF_RECENT_SCORES_TO_DISPLAY));
            List<Routine> recentRoutines = last30DaysStats.routinesAttempted().stream()
                    .map(routineId -> routineService.getRoutineById(routineId))
                    .filter(routineOpt -> routineOpt.isPresent())
                    .map(routineOpt -> routineOpt.get())
                    .collect(Collectors.toList());
            model.addAttribute("recentRoutines", recentRoutines);
        } else if (user != null) {
            log.debug("Logged in user {} has no recent activity, so displaying information homepage", user.getName());
        }
        // Only show random routine if user has no recent scores
        if (!haveLast30DaysStats) {
            model.addAttribute("routine", routineService.getRandomRoutine());
        }
        return "index";
    }
}
