package com.snookerup.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Integration tests for the IndexController class.
 *
 * @author Huw
 */
@WebMvcTest(IndexController.class)
class IndexControllerITTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getIndex() throws Exception {
        this.mockMvc
                .perform(get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
