package com.pearteam.demobackend.controllers.api;

import com.pearteam.demobackend.domain.TokenHolder;
import com.pearteam.demobackend.services.exceptions.AuthException;
import com.pearteam.demobackend.services.AuthService;
import com.pearteam.demobackend.services.exceptions.DataAccessException;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/v1/vis-test")
@Log
public class AuthController {
	@Autowired
	private AuthService authService;

	@PostMapping(value = "login", consumes = "application/json", produces = "application/json")
	public ResponseEntity<TokenHolder> login (
			@RequestBody Map<String, String> credentials
	) {
		Integer userId;
		try {
			userId = authService.validateCredentials(credentials.get("user"), credentials.get("password"));
		} catch (AuthException ex){
			log.severe("Invalid username / password");
			return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
		} catch (DataAccessException ex){
			log.severe("Database error");
			return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		TokenHolder token;
		try {
			token = authService.issueToken(userId);
		} catch (DataAccessException ex) {
			log.severe("Database not accessible");
			return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(token, new HttpHeaders(), HttpStatus.OK);
	}
}
