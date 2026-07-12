package com.snookerup.controllers;

import com.snookerup.model.db.nosql.Routine;
import com.snookerup.services.RoutineService;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * Controller serving all routes related to routines.
 *
 * @author Huw
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class RoutineController {

    /** The tag indicating all routines. */
    private static final String ALL_TAG = "all";

    /** Service to get routines from. */
    private final RoutineService routineService;

    @GetMapping("/routines")
    public String getRoutines(Model model,
                                 @RequestParam(value = "tag", required = false, defaultValue = ALL_TAG) String tag,
                                 @RequestParam(value = "search", required = false, defaultValue = "") String searchTerm,
                                 @RequestParam(value = "page", defaultValue = "0") @PositiveOrZero int userPage,
                                 @RequestParam(value = "size", defaultValue = "18") @Positive int size) {
        log.debug("getAllRoutines tag={}", tag);
        // Users expect page number to start at 1, but it actually starts at 0, so translate by subtracting one
        int page;
        if (userPage <= 0) {
            page = 0;
        } else {
            page = userPage - 1;
        }
        String tagSearch;
        if (tag.equals(ALL_TAG)) {
            tagSearch = null;
        } else {
            tagSearch = tag;
        }
        model.addAttribute("routines", routineService.getRoutines(tagSearch, searchTerm, page, size));
        model.addAttribute("tags", routineService.getAllTags());
        model.addAttribute("selectedTag", tag);
        model.addAttribute("currentPage", page);
        model.addAttribute("searchTerm", searchTerm);
        return "routines";
    }

    @GetMapping("/routines/{id}")
    public String getRoutineById(@PathVariable("id") String id, Model model) {
        Optional<Routine> routineOpt = routineService.getRoutineById(id);
        routineOpt.ifPresent((routine) -> {
            model.addAttribute("routine", routineOpt.get());
        });
        return "routine";
    }
}
