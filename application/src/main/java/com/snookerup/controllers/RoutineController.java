package com.snookerup.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller serving all routes related to routines.
 *
 * @author Huw
 */
@Controller
@RequestMapping("/routines")
public class RoutineController {

    @GetMapping()
    public String getAllRoutines() {
        return "routines";
    }

    @GetMapping("/{id}")
    public String getRoutineById(@PathVariable("id") String id) {
        return "routine";
    }
}
