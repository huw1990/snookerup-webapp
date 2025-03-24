package com.snookerup.controllers;

//import com.snookerup.BaseTestcontainersIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Integration tests for the IndexController class.
 *
 * @author Huw
 */
@ActiveProfiles("dev")
@SpringBootTest
@AutoConfigureMockMvc
class IndexControllerTestsIT {//extends BaseTestcontainersIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getIndex() throws Exception {
        this.mockMvc
                .perform(get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
