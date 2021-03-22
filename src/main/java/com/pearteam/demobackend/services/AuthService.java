package com.pearteam.demobackend.services;

import com.pearteam.demobackend.domain.TokenHolder;
import com.pearteam.demobackend.domain.User;
import com.pearteam.demobackend.services.exceptions.AuthException;
import com.pearteam.demobackend.services.exceptions.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

	@Autowired
	UserService userService;

	@Autowired
	RandomizeService randomService;

	public Integer validateCredentials(String username, String password) throws AuthException, DataAccessException {
		Integer userId = userService.checkUserExistsAndPasswordCorrect(username, password);
		if (userId == null) {
			if (username.equals("admin") && (userService.findByName("admin") == null)) {
				userId = userService.createAdmin();
			} else {
				throw new AuthException();
			}
		}
		return userId;
	}

	public TokenHolder issueToken(Integer userId) throws DataAccessException {
		String token = randomService.generateToken();
		try {
			userService.saveToken(userId, token);
		} catch (Exception ex) {
			throw new DataAccessException();
		}
		return new TokenHolder(token);
	}

	public User authenticate(String token) throws AuthException {
		User user = userService.findByToken(token);
		if (user == null) {
			throw new AuthException();
		}
		return user;
	}
}
