package com.pearteam.demobackend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Setter
@NoArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	@Column(unique = true)
	@NotNull
	private String username;
	@JsonIgnore
	@NotNull
	private String password;
	@ManyToOne
	@NotNull
	private Role role;
	@JsonIgnore
	private String token;
	@JsonIgnore
	private LocalDateTime tokenExpirationDate;
	@JsonIgnore
	private LocalDateTime dateCreated;
	@JsonIgnore
	private LocalDateTime lastUpdated;


	public User(String username, String password, Role role) {
		this.username = username;
		this.password = password;
		this.role = role;
	}
}
