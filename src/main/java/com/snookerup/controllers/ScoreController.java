package com.snookerup.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller serving all routes related to scores.
 *
 * @author Huw
 */
@Controller
@RequestMapping("/scores")
public class ScoreController {

    @GetMapping()
    public String getAllScores() {
        return "scores";
    }
}
