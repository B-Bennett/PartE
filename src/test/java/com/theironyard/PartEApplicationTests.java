package com.theironyard;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PartEApplication.class)
@WebAppConfiguration
public class PartEApplicationTests {

	@Autowired
	UserRepository users;

	@Autowired
	EntertainmentRepository entertainments;

	@Autowired
	WebApplicationContext wap;

	MockMvc mockMvc;

	@Before
	public void before() {
		entertainments.deleteAll();
		users.deleteAll();
		mockMvc = MockMvcBuilders.webAppContextSetup(wap).build(); // mock tests
	}
	@Test
	public void testLogin() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.post("/login")
						.param("username", "testUser")
						.param("password", "testPass")
		);

		assertTrue(users.count() == 1);
	}
	@Test
	public void testAddEvent() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.post("/addEntertainment")
						.param("name", "Test name")
						.param("type", "type")
						.param("price", "price")
						.param("rating", "rating")
						.param("comment", "comment")

						.sessionAttr("username", "testUser")
		);

		assertTrue(entertainments.count() == 1);
	}
	
}
