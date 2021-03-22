package com.pearteam.demobackend.domain.repositories;

import com.pearteam.demobackend.domain.Role;
import com.pearteam.demobackend.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
	User findById(@Param("id") int id);
	User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
	User findByToken(@Param("token") String token);
	User findByUsername(@Param("username") String username);
	List<User> findAll();
	List<User> findByRole(@Param("role") Role role);
}
