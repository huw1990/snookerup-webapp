package com.snookerup;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
@SpringBootTest
@AutoConfigureMockMvc
class SnookerupWebappApplicationTests {//extends BaseTestcontainersIT {

	@Test
	void contextLoads() {
	}

}
