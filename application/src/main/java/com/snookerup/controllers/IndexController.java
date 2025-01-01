package com.snookerup.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller serving the templated SnookerUp homepage.
 *
 * @author Huw
 */
@Controller
public class IndexController {

    @GetMapping("/")
    public String getIndex() {
        return "index";
    }
}
