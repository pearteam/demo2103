package com.pearteam.demobackend.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TokenHolder {
	private String token;
	public TokenHolder(String token) {
		this.token = token;
	}
}
