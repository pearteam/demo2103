package com.pearteam.demobackend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Entity
@Setter
@NoArgsConstructor
public class Role {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	@NotNull
	String roleName;
	@JsonIgnore
	private LocalDateTime dateCreated;
	@JsonIgnore
	private LocalDateTime lastUpdated;

	public Role(String roleName){
		this.roleName = roleName;
	}
}
