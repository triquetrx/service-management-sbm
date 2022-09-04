package com.cognizant.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public @Data @AllArgsConstructor @NoArgsConstructor class ServiceRequestDTO {
	
	private long productId;
	private String problem;
	private String description;

}
