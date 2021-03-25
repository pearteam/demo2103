package com.pearteam.demobackend.services;

import com.pearteam.demobackend.domain.*;
import com.pearteam.demobackend.domain.RolePermission;
import com.pearteam.demobackend.domain.repositories.RolePermissionRepository;
import com.pearteam.demobackend.domain.repositories.UserRepository;
import com.pearteam.demobackend.services.exceptions.DataAccessException;
import com.pearteam.demobackend.services.exceptions.InvalidInputDataException;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Log
public class UserService {

	@Autowired
	UserRepository userRepository;
	@Autowired
	RolePermissionRepository rolePermissionRepository;
	@Autowired
	RoleService roleService;

	public static final String USERNAME_ALLOWED_CHARACTERS="[a-zA-Z0-9_]+";

	public Integer checkUserExistsAndPasswordCorrect(String username, String password) {
		User user = userRepository.findByUsernameAndPassword(username, password);
		if (user != null) {
			return user.getId();
		}
		return null;
	}

	public void saveToken(int userId, String token) {
		User user = userRepository.findById(userId);
		user.setLastUpdated(LocalDateTime.now());
		user.setTokenExpirationDate(LocalDateTime.now().plusDays(1));
		user.setToken(token);
		userRepository.save(user);
	}

	public boolean canUserDoAction(User user, Permission permission) {
		Role userRole = user.getRole();
		RolePermission rolePermission = rolePermissionRepository.findByRoleAndPermission(userRole, permission);
		if (rolePermission == null) {
			return false;
		}
		return true;
	}

	public boolean canListUsers(User user) {
		return canUserDoAction(user, Permission.LIST_USERS);
	}

	public boolean canCreateUsers(User user) {
		return canUserDoAction(user, Permission.CREATE_USERS);
	}

	public boolean canUpdateUsers(User user) {
		return canUserDoAction(user, Permission.EDIT_USERS);
	}

	public boolean canDeleteUsers(User user) {
		return canUserDoAction(user, Permission.DELETE_USERS);
	}


	public List<User> findAll() {
		return userRepository.findAll();
	}

	public User findByToken(String token) {
		User user = userRepository.findByToken(token);
		if (user != null && user.getTokenExpirationDate().isBefore(LocalDateTime.now())) {
			return null;
		}
		return user;
	}

	public User findByName(String name) {
		return userRepository.findByUsername(name);
	}

	public User addUser(Map<String, String> newUserData) throws InvalidInputDataException, DataAccessException {
		User newUser = validateNewUserDataAndCreateUser(newUserData);
		LocalDateTime now = LocalDateTime.now();
		newUser.setDateCreated(now);
		newUser.setLastUpdated(now);
		try {
			userRepository.save(newUser);
		} catch (Exception ex) {
			throw new DataAccessException();
		}
		return newUser;
	}

	public User updateUser(User updatedUser, Map<String, String> updateUserData) throws InvalidInputDataException {
		LocalDateTime now = LocalDateTime.now();
		updatedUser.setLastUpdated(now);
		updatedUser = validateUpdateUserDataAndApplyChanges(updatedUser, updateUserData);
		userRepository.save(updatedUser);
		return updatedUser;
	}

	private User validateNewUserDataAndCreateUser(Map<String, String> newUserData) throws InvalidInputDataException {
		if (newUserData == null || newUserData.size() == 0) {
			throw new InvalidInputDataException("Missing user data");
		}
		String username = newUserData.get("username");
		if (username == null || username.length() == 0 || !username.matches(USERNAME_ALLOWED_CHARACTERS)) {
			throw new InvalidInputDataException("Invalid username");
		}
		String password = newUserData.get("password");
		if (password == null || password.length() == 0) {
			throw new InvalidInputDataException("Invalid password");
		}
		String roleIdString = newUserData.get("roleId");
		if (roleIdString == null || roleIdString.length() == 0) {
			throw new InvalidInputDataException("Invalid roleId");
		}
		int roleId;
		try {
			roleId = Integer.parseInt(roleIdString);
		} catch (NumberFormatException ex) {
			throw new InvalidInputDataException("Invalid roleId");
		}
		Role role = roleService.getRole(roleId);
		if (role == null) {
			throw new InvalidInputDataException("Invalid roleId");
		}
		return new User(username, password, role);
	}

	private User validateUpdateUserDataAndApplyChanges(User updatedUser, Map<String, String> updateUserData) throws InvalidInputDataException {
		if (updateUserData == null || updateUserData.size() == 0) {
			throw new InvalidInputDataException("Missing user data");
		}
		String username = updateUserData.get("username");
		if (username != null) {
			if (username.length() == 0 || !username.matches(USERNAME_ALLOWED_CHARACTERS)) {
				throw new InvalidInputDataException("Invalid username");
			}
			updatedUser.setUsername(username);
		}
		String roleIdString = updateUserData.get("roleId");
		if (roleIdString != null) {
			if (roleIdString.length() == 0) {
				throw new InvalidInputDataException("Invalid roleId");
			}
			int roleId;
			try {
				roleId = Integer.parseInt(roleIdString);
			} catch (NumberFormatException ex) {
				throw new InvalidInputDataException("Invalid roleId");
			}
			Role role = roleService.getRole(roleId);
			if (role == null) {
				throw new InvalidInputDataException("Invalid roleId");
			}
			updatedUser.setRole(role);
		}
		return updatedUser;
	}

	public User getUser(int userId) {
		return userRepository.findById(userId);
	}

	public void deleteUser(User userToDelete) {
		userRepository.delete(userToDelete);
	}

	public boolean isAdmin(User performingUser) {
		return performingUser.getUsername().equals("admin");
	}

	public Integer createAdmin() throws DataAccessException {
		RoleWithPermission roleWithPermission = new RoleWithPermission();
		roleWithPermission.setName("ADMIN_ROLE");
		List<Permission> permissions = new ArrayList<>();
		permissions.add(Permission.LIST_USERS);
		permissions.add(Permission.CREATE_USERS);
		permissions.add(Permission.EDIT_USERS);
		permissions.add(Permission.DELETE_USERS);
		roleWithPermission.setPermissions(permissions);
		Role newAdminRole = null;
		try {
			newAdminRole = roleService.addRole(roleWithPermission);
		} catch (InvalidInputDataException e) {
			log.severe("Error adding role for admin");
		}
		User adminUser = new User("admin", "admin", newAdminRole);
		LocalDateTime now = LocalDateTime.now();
		adminUser.setDateCreated(now);
		adminUser.setLastUpdated(now);
		try {
			userRepository.save(adminUser);
		} catch (Exception ex) {
			throw new DataAccessException();
		}
		return adminUser.getId();
	}
}
