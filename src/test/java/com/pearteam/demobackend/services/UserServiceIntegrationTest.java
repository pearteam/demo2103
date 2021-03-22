package com.pearteam.demobackend.services;

import com.pearteam.demobackend.domain.Permission;
import com.pearteam.demobackend.domain.Role;
import com.pearteam.demobackend.domain.RoleWithPermission;
import com.pearteam.demobackend.domain.User;
import com.pearteam.demobackend.domain.repositories.UserRepository;
import com.pearteam.demobackend.services.exceptions.DataAccessException;
import com.pearteam.demobackend.services.exceptions.InvalidInputDataException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceIntegrationTest {

	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserRepository userRepository;

	@Test
	@DisplayName("User data are saved and retrieved from database")
	void addUser() throws InvalidInputDataException, DataAccessException {
		//given
		Integer roleId = insertCreateUserRole().getId();

		//when
		Map newUserData = new HashMap();
		newUserData.put("username", "Joe");
		newUserData.put("password", "joepassword");
		newUserData.put("roleId", roleId.toString());
		userService.addUser(newUserData);

		//then
		List<User> usersFromDb = userRepository.findAll();
		assertEquals(usersFromDb.size(), 1);
		User user = usersFromDb.get(0);
		assertEquals(user.getUsername(), "Joe");
		assertEquals(user.getPassword(), "joepassword");
		assertEquals(user.getRole().getId(), roleId);
	}

	private Role insertCreateUserRole() throws InvalidInputDataException {
		RoleWithPermission roleWithPermission = new RoleWithPermission();
		roleWithPermission.setName("TEST_ROLE");
		List permissions = new ArrayList<>();
		permissions.add(Permission.CREATE_USERS);
		roleWithPermission.setPermissions(permissions);
		Role role = roleService.addRole(roleWithPermission);
		return role;
	}
}