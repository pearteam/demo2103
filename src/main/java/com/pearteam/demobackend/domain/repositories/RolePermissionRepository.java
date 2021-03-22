package com.pearteam.demobackend.domain.repositories;

import com.pearteam.demobackend.domain.Permission;
import com.pearteam.demobackend.domain.Role;
import com.pearteam.demobackend.domain.RolePermission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends CrudRepository<RolePermission, Integer> {
	List<RolePermission> findAllByRole(@Param("role") Role role);
	RolePermission findByRoleAndPermission(@Param("role") Role role, @Param("permission") Permission permission);
}
