package com.snookerup.controllers;

import com.snookerup.services.RoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller serving the templated SnookerUp homepage.
 *
 * @author Huw
 */
@Controller
@RequiredArgsConstructor
public class IndexController {

    private final RoutineService routineService;

    @GetMapping("/")
    public String getIndex(Model model) {
        model.addAttribute("routine", routineService.getRandomRoutine());
        return "index";
    }
}
