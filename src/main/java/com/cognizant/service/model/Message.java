package com.cognizant.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public @Data @AllArgsConstructor @NoArgsConstructor class Message {
	
	private int status;
	private String message;
	private Object payload;

}
