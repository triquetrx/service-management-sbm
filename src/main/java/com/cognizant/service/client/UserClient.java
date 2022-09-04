package com.cognizant.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.cognizant.service.model.Message;

@FeignClient(name = "user-microservice", url="http://localhost:8002/user")
public interface UserClient {

	@GetMapping("/me")
	Message getCurrentUserDetails(@RequestHeader(name = "Authorization")String token);
	
}
