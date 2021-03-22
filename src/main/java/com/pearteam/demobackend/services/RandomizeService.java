package com.pearteam.demobackend.services;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RandomizeService {

	public String generateToken() {
		return UUID.randomUUID().toString();
	}
}
