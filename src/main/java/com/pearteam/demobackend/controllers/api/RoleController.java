package com.pearteam.demobackend.controllers.api;

import com.pearteam.demobackend.domain.Role;
import com.pearteam.demobackend.domain.RoleWithPermission;
import com.pearteam.demobackend.domain.User;
import com.pearteam.demobackend.services.*;
import com.pearteam.demobackend.services.exceptions.AuthException;
import com.pearteam.demobackend.services.exceptions.InvalidInputDataException;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/v1/vis-test/roles")
@Log
public class RoleController {
	@Autowired
	AuthService authService;
	@Autowired
	UserService userService;
	@Autowired
	RoleService roleService;

	@GetMapping(produces = "application/json")
	public ResponseEntity<List<RoleWithPermission>> getRoles(@RequestHeader("token") String token) {
		User performingUser;
		try {
			performingUser = authService.authenticate(token);
		} catch (AuthException e) {
			log.severe("Invalid token, please log in again");
			return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
		}
		if (!userService.isAdmin(performingUser)) {
			log.severe("User don't have permission to list roles");
			return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.FORBIDDEN);
		}
		List<RoleWithPermission> roles = roleService.getAllWithPermissions();
		if (roles.size() == 0) {
			log.severe("Something went wrong, missing roles");
			return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(roles, new HttpHeaders(), HttpStatus.OK);
	}

	@PostMapping(consumes = "application/json", produces = "application/json")
	public ResponseEntity<RoleWithPermission> createRole(@RequestHeader("token") String token,
														 @RequestBody RoleWithPermission newRoleData) {
		User performingUser;
		try {
			performingUser = authService.authenticate(token);
		} catch (AuthException e) {
			log.severe("Invalid token, please log in again");
			return new ResponseEntity(null, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
		}
		if (!userService.isAdmin(performingUser)) {
			log.severe("User don't have permission to create roles");
			return new ResponseEntity(null, new HttpHeaders(), HttpStatus.FORBIDDEN);
		}
		RoleWithPermission newRole;
		try {
			newRole = roleService.addRoleAndSeePermissions(newRoleData);
		} catch (InvalidInputDataException e) {
			log.severe(e.getMessage());
			return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity(newRole, new HttpHeaders(), HttpStatus.OK);
	}

	@PutMapping(path = "/{roleId}", consumes = "application/json", produces = "application/json")
	public ResponseEntity<RoleWithPermission> updateRole(@RequestHeader("token") String token,
														 @RequestBody RoleWithPermission updatedRoleData,
														 @PathVariable int roleId) {
		User performingUser;
		try {
			performingUser = authService.authenticate(token);
		} catch (AuthException e) {
			log.severe("Invalid token, please log in again");
			return new ResponseEntity(null, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
		}
		if (!userService.isAdmin(performingUser)) {
			log.severe("User don't have permission to update roles");
			return new ResponseEntity(null, new HttpHeaders(), HttpStatus.FORBIDDEN);
		}
		Role roleToUpdate = roleService.getRole(roleId);
		if (roleToUpdate == null) {
			log.severe("Role not found");
			return new ResponseEntity(null, new HttpHeaders(), HttpStatus.NOT_FOUND);
		}
		RoleWithPermission roleUpdated;
		try {
			roleUpdated = roleService.updateRole(roleToUpdate, updatedRoleData);
		} catch (InvalidInputDataException e) {
			log.severe(e.getMessage());
			return new ResponseEntity(null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity(roleUpdated, new HttpHeaders(), HttpStatus.OK);
	}

	@DeleteMapping(path = "/{roleId}")
	public ResponseEntity deleteRole(@RequestHeader("token") String token,
									 @PathVariable int roleId) {
		User performingUser;
		try {
			performingUser = authService.authenticate(token);
		} catch (AuthException e) {
			log.severe("Invalid token, please log in again");
			return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
		}
		if (!userService.isAdmin(performingUser)) {
			log.severe("User don't have permission to remove roles");
			return new ResponseEntity(null, new HttpHeaders(), HttpStatus.FORBIDDEN);
		}
		Role roleToDelete = roleService.getRole(roleId);
		if (roleToDelete == null) {
			log.severe("Role not found");
			return new ResponseEntity(null, new HttpHeaders(), HttpStatus.NOT_FOUND);
		}
		try {
			roleService.deleteRole(roleToDelete);
		} catch (Exception ex) {
			log.severe("Role wasn't deleted: " + ex.getMessage());
			return new ResponseEntity(null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity(null, new HttpHeaders(), HttpStatus.OK);
	}
}
