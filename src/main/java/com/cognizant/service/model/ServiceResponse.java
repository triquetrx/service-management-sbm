package com.cognizant.service.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public @Data @AllArgsConstructor @NoArgsConstructor class ServiceResponse {

	private long id;
	private long productId;
	private UserData user;
	private Date requestDate;
	private String problem;
	private String description;
	private ServiceStatus status;
	
}
