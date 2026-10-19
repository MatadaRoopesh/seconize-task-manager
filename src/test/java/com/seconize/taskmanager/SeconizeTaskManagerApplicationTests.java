package com.seconize.taskmanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"APP_AUTH_USERNAME=testuser",
		"APP_AUTH_PASSWORD=testpassword"
})
class SeconizeTaskManagerApplicationTests {

	@Test
	void contextLoads() {
	}
}