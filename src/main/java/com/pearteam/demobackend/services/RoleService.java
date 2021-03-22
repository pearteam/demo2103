package com.pearteam.demobackend.services;

import com.pearteam.demobackend.domain.*;
import com.pearteam.demobackend.domain.RolePermission;
import com.pearteam.demobackend.domain.repositories.RolePermissionRepository;
import com.pearteam.demobackend.domain.repositories.RoleRepository;
import com.pearteam.demobackend.domain.repositories.UserRepository;
import com.pearteam.demobackend.services.exceptions.InvalidInputDataException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	RolePermissionRepository rolePermissionRepository;

	@Autowired
	UserRepository userRepository;

	public List<RoleWithPermission> getAllWithPermissions() {
		List<Role> roles = roleRepository.findAll();
		List<RoleWithPermission> roleWithPermissions = new ArrayList<>();
		for (Role role : roles) {
			RoleWithPermission roleWithPermission = getRoleWithPermission(role);
			roleWithPermissions.add(roleWithPermission);
		}
		return roleWithPermissions;
	}

	@NotNull
	private RoleWithPermission getRoleWithPermission(Role role) {
		RoleWithPermission roleWithPermission = new RoleWithPermission();
		roleWithPermission.setId(role.getId());
		roleWithPermission.setName(role.getRoleName());
		List<Permission> permissions = new ArrayList<>();
		for (RolePermission rolePermission : rolePermissionRepository.findAllByRole(role)) {
			permissions.add(rolePermission.getPermission());
		}
		roleWithPermission.setPermissions(permissions);
		return roleWithPermission;
	}

	public RoleWithPermission addRoleAndSeePermissions(RoleWithPermission newRoleData) throws InvalidInputDataException {
		return getRoleWithPermission(addRole(newRoleData));
	}

	public Role addRole(RoleWithPermission newRoleData) throws InvalidInputDataException {
		Role newRole = validateNewRoleDataAndCreateRole(newRoleData);
		LocalDateTime now = LocalDateTime.now();
		newRole.setDateCreated(now);
		newRole.setLastUpdated(now);
		roleRepository.save(newRole);
		return newRole;
	}

	private Role validateNewRoleDataAndCreateRole(RoleWithPermission newRoleData) throws InvalidInputDataException {
		String name = newRoleData.getName();
		if (name == null || name.length() == 0 || !name.matches("[a-zA-Z0-9_]+")) {
			throw new InvalidInputDataException("Invalid role name");
		}
		Role role = roleRepository.findByRoleName(name);
		if (role != null) {
			throw new InvalidInputDataException("Role already exists");
		}
		role = new Role(name);
		List<Permission> permissions = newRoleData.getPermissions();
		if (permissions == null || permissions.size() == 0) {
			throw new InvalidInputDataException("Missing permissions");
		}
		roleRepository.save(role);
		createPermissions(role, permissions);
		return role;
	}

	private void createPermissions(Role role, List<Permission> permissions) {
		for (Permission permission : permissions) {
			RolePermission rolePermission = new RolePermission();
			rolePermission.setPermission(permission);
			rolePermission.setRole(role);
			rolePermission.setDateCreated(LocalDateTime.now());
			rolePermissionRepository.save(rolePermission);
		}
	}

	public Role getRole(int roleId) {
		return roleRepository.findById(roleId);
	}

	public RoleWithPermission updateRole(Role roleToUpdate, RoleWithPermission updatedRoleData) throws InvalidInputDataException {
		String name = updatedRoleData.getName();
		if (name != null) {
			if (name.length() == 0 || !name.matches("[a-zA-Z0-9_]+")) {
				throw new InvalidInputDataException("Invalid role name");
			}
			roleToUpdate.setRoleName(name);
			roleRepository.save(roleToUpdate);
		}
		List<Permission> permissions = updatedRoleData.getPermissions();
		if (permissions != null) {
			if (permissions.size() == 0) {
				throw new InvalidInputDataException("Missing permissions");
			}
			removePermissions(roleToUpdate);
			createPermissions(roleToUpdate, permissions);
		}
		return getRoleWithPermission(roleToUpdate);
	}

	private void removePermissions(Role roleToUpdate) {
		List<RolePermission> rolePermissions = rolePermissionRepository.findAllByRole(roleToUpdate);
		for (RolePermission rolePermission : rolePermissions) {
			rolePermissionRepository.delete(rolePermission);
		}
	}

	public void deleteRole(Role roleToDelete) throws InvalidInputDataException {
		List<User> usersWithThisRole = userRepository.findByRole(roleToDelete);
		if (usersWithThisRole.size() > 0) {
			throw new InvalidInputDataException("There are users with this role");
		}
		removePermissions(roleToDelete);
		roleRepository.delete(roleToDelete);
	}
}
