package com.snookerup.controllers;

import com.snookerup.services.RoutineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * Controller serving all routes related to routines.
 *
 * @author Huw
 */
@Controller
@RequestMapping("/routines")
@RequiredArgsConstructor
@Slf4j
public class RoutineController {

    /** The tag indicating all routines. */
    private static final String ALL_TAG = "all";

    /** Service to get routines from. */
    private final RoutineService routineService;

    @GetMapping()
    public String getAllRoutines(Model model, @RequestParam Optional<String> tag) {
        log.debug("getAllRoutines tag={}", tag);
        if (tag.isPresent() && !tag.get().equals(ALL_TAG)) {
            model.addAttribute("routines", routineService.getRoutinesForTag(tag.get()));
        } else {
            model.addAttribute("routines", routineService.getAllRoutines());
        }
        model.addAttribute("tags", routineService.getAllTags());
        String selectedTag = tag.orElse(ALL_TAG);
        model.addAttribute("selectedTag", selectedTag);
        return "routines";
    }

    @GetMapping("/{id}")
    public String getRoutineById(@PathVariable("id") String id, Model model) {
        model.addAttribute("routine", routineService.getRoutineById(id));
        return "routine";
    }
}
