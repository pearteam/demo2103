package com.pearteam.demobackend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Setter
public class RolePermission {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	@NotNull
	@ManyToOne
	Role role;
	@NotNull
	@Enumerated(EnumType.STRING)
	Permission permission;
	@JsonIgnore
	private LocalDateTime dateCreated;
}
