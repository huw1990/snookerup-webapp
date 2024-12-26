package com.snookerup.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Integration tests for the ScoreController class.
 *
 * @author Huw
 */
@WebMvcTest(ScoreController.class)
class ScoreControllerTestsIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllScores() throws Exception {
        this.mockMvc
                .perform(get("/scores"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
