package com.snookerup.controllers;

import com.snookerup.services.PracticeSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller serving all routes related to routines.
 *
 * @author Huw
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class PracticeSessionController {

    /** Service to get practice sessions from. */
    private final PracticeSessionService practiceSessionService;

    @GetMapping("/practicesessions")
    public String getAllPracticeSessions(Model model, @AuthenticationPrincipal OidcUser user) {
        model.addAttribute("practiceSessions",
                practiceSessionService.getPracticeSessionsForPlayerUsername(user.getName()));
        return "practiceSessions";
    }

    @GetMapping("/practicesessions/{id}")
    public String getPracticeSessionById(@PathVariable("id") String id, Model model, @AuthenticationPrincipal OidcUser user) {
        model.addAttribute("practiceSession",
                practiceSessionService.getPracticeSessionByIdAndPlayerUsername(id, user.getName()));
        return "practiceSession";
    }
}