package com.pearteam.demobackend.domain.repositories;

import com.pearteam.demobackend.domain.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {
	Role findById(@Param("id") int id);
	Role findByRoleName(@Param("roleName") String roleName);
	List<Role> findAll();
}
