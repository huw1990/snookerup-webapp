package com.snookerup.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller serving all routes related to routines.
 *
 * @author Huw
 */
@Controller
public class RoutineController {

    @GetMapping("/routines")
    public String getAllRoutines() {
        return "routines";
    }
}
