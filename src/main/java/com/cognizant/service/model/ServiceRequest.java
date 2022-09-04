package com.cognizant.service.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
public @Data @AllArgsConstructor @NoArgsConstructor class ServiceRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private long productId;
	private long userId;
	private Date requestDate;
	private String problem;
	private String description;
	private ServiceStatus status;

	public ServiceRequest(long productId, long userId, Date requestDate, String problem, String description,
			ServiceStatus status) {
		super();
		this.productId = productId;
		this.userId = userId;
		this.requestDate = requestDate;
		this.problem = problem;
		this.description = description;
		this.status = status;
	}

}
