package com.pearteam.demobackend.controllers.api;

import com.pearteam.demobackend.domain.TokenHolder;
import com.pearteam.demobackend.services.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerUnitTest {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private AuthService authService;

	@Test
	void shouldReturnToken() throws Exception {
		//when

			when(authService.issueToken(any())).thenReturn(new TokenHolder("1234-5678-9012"));
			when(authService.validateCredentials(any(), any())).thenReturn(1);
		//then
			mockMvc.perform(post("/v1/vis-test/login").contentType(MediaType.APPLICATION_JSON).content(
			"{\"user\": \"admin\", \"password\":\"admin\"}")).andExpect(
			status().isOk()).andExpect(content().json("{\"token\": \"1234-5678-9012\"}"));
	}

}