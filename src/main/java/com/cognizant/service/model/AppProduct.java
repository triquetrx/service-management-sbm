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
public @Data @AllArgsConstructor @NoArgsConstructor class AppProduct {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;
	private String make;
	private String model;
	private double cost;
	private Date createdDate;
	private String createdBy;

	public AppProduct(String name, String make, String model, double cost, Date date, String createdBy) {
		this.name = name;
		this.make = make;
		this.model = model;
		this.cost = cost;
		this.createdDate = date;
		this.createdBy = createdBy;
	}
}
