package com.cognizant.service.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public @Data @AllArgsConstructor @NoArgsConstructor class Users {

	private long id;
	private String name;
	private String email;
	private long mobile;
	private Date registrationDate;
	private Date birthDate;
	private String gender;
	private String role;
	private boolean active;

}
