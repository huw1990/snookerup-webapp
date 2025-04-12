package com.snookerup.controllers;

import com.snookerup.BaseTestcontainersIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Integration tests for the RoutineController class.
 *
 * @author Huw
 */
@ActiveProfiles("dev")
@SpringBootTest
@AutoConfigureMockMvc
class RoutineControllerTestsIT extends BaseTestcontainersIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllRoutines() throws Exception {
        this.mockMvc
                .perform(get("/routines"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getRoutineById() throws Exception {
        this.mockMvc
                .perform(get("/routines/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
