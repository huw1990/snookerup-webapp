package com.snookerup.controllers;

import com.snookerup.model.db.nosql.RoutineOverview;
import com.snookerup.services.RoutineService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * A Spring REST controller providing REST endpoints related to routines, typically made as extra in-browser requests
 * rather than as part of loading a new page with Thymeleaf.
 *
 * @author Huw
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class RoutineRestController {

    private final RoutineService routineService;

    /**
     * Get routine overviews (i.e. just the routine ID and title) for a given search term.
     * @param searchTerm The search term, searching for a routine title
     * @param page The page number, starting from 0
     * @param size The page size
     * @return A page of routine overviews
     */
    @GetMapping("/routine-overviews")
    public Page<RoutineOverview> getRoutineOverviews(@RequestParam(value = "search", required = false, defaultValue = "") @NotBlank String searchTerm,
                                                     @RequestParam(value = "page", defaultValue = "0") @PositiveOrZero int page,
                                                     @RequestParam(value = "size", defaultValue = "18") @Positive int size) {
        return routineService.getRoutineOverviews(searchTerm, page, size);
    }
}
