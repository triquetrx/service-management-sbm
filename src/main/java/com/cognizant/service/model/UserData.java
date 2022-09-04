package com.cognizant.service.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
public @Data @AllArgsConstructor @NoArgsConstructor class UserData {

	
	@Id
	private long userId;
	private String name;
	private String email;
	private long mobile;
}
