package com.example.inventory_management;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // loads application-test.properties (H2) instead of the real MySQL config
class InventoryManagementApplicationTests {

	@Test
	void contextLoads() {
	}

}
