package com.pearteam.demobackend.controllers.api;

import com.pearteam.demobackend.domain.User;
import com.pearteam.demobackend.services.exceptions.AuthException;
import com.pearteam.demobackend.services.AuthService;
import com.pearteam.demobackend.services.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/v1/vis-test/users")
@Log
public class UserController {
	@Autowired
	AuthService authService;
	@Autowired
	UserService userService;

	@GetMapping(produces = "application/json")
	public ResponseEntity<List<User>> getUsers(@RequestHeader("token") String token) {
		User performingUser;
		try {
			performingUser = authService.authenticate(token);
		} catch (AuthException e) {
			log.severe("Invalid token, please log in again");
			return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
		}
		if (!userService.canListUsers(performingUser)) {
			log.severe("User don't have permission to list users");
			return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.FORBIDDEN);
		}
		List<User> users = userService.findAll();
		if (users.size() == 0) {
			log.severe("Something went wrong, missing users");
			return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(users, new HttpHeaders(), HttpStatus.OK);
	}

	@PostMapping(consumes = "application/json", produces = "application/json")
	public ResponseEntity<User> createUser(@RequestHeader("token") String token, @RequestBody Map<String, String> newUserData) {
		User performingUser;
		try {
			performingUser = authService.authenticate(token);
		} catch (AuthException e) {
			log.severe("Invalid token, please log in again");
			return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
		}
		if (!userService.canCreateUsers(performingUser)) {
			log.severe("User don't have permission to create users");
			return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.FORBIDDEN);
		}
		User newUser;
		try {
			newUser = userService.addUser(newUserData);
		} catch (Exception e) {
			log.severe(e.getMessage());
			return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(newUser, new HttpHeaders(), HttpStatus.OK);
	}

	@PutMapping(path = "/{userId}", consumes = "application/json", produces = "application/json")
	public ResponseEntity<User> updateUser(@RequestHeader("token") String token,
										   @RequestBody Map<String, String> updatedUserData,
										   @PathVariable int userId) {
		User performingUser;
		try {
			performingUser = authService.authenticate(token);
		} catch (AuthException e) {
			log.severe("Invalid token, please log in again");
			return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
		}
		if (!userService.canUpdateUsers(performingUser)) {
			log.severe("User don't have permission to update users");
			return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.FORBIDDEN);
		}
		User userToUpdate = userService.getUser(userId);
		if (userToUpdate == null) {
			log.severe("User not found");
			return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.NOT_FOUND);
		}
		try {
			userToUpdate = userService.updateUser(userToUpdate, updatedUserData);
		} catch (Exception e) {
			log.severe(e.getMessage());
			return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(userToUpdate, new HttpHeaders(), HttpStatus.OK);
	}

	@DeleteMapping(path = "/{userId}")
	public ResponseEntity deleteUser(@RequestHeader("token") String token,
									 @PathVariable int userId) {
		User performingUser;
		try {
			performingUser = authService.authenticate(token);
		} catch (AuthException e) {
			log.severe("Invalid token, please log in again");
			return new ResponseEntity(null, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
		}
		if (!userService.canDeleteUsers(performingUser)) {
			log.severe("User don't have permission to delete users");
			return new ResponseEntity(null, new HttpHeaders(), HttpStatus.FORBIDDEN);
		}
		User userToDelete = userService.getUser(userId);
		if (userToDelete == null) {
			log.severe("User not found");
			return new ResponseEntity(null, new HttpHeaders(), HttpStatus.NOT_FOUND);
		}
		try {
			userService.deleteUser(userToDelete);
		} catch (Exception ex) {
			log.severe("User wasn't deleted: " + ex.getMessage());
			return new ResponseEntity(null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity(null, new HttpHeaders(), HttpStatus.OK);
	}
}
