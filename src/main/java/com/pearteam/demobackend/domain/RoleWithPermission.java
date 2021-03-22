package com.pearteam.demobackend.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoleWithPermission {
	private Integer id;
	private String name;
	private List<Permission> permissions;
}
