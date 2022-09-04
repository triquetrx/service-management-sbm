package com.cognizant.service.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public @Data @AllArgsConstructor @NoArgsConstructor class AppProduct {
	
	private long id;
	private String name;
	private String make;
	private String model;
	private double cost;
	private Date createdDate;
	private String createdBy;
	private String productImageUrl;
}
