package com.cognizant.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.cognizant.service.dto.ValidatingDTO;

@FeignClient(name = "auth-microservice", url = "http://localhost:8001")
public interface AuthClient {

	@GetMapping("/validate")
	ValidatingDTO validatingToken(@RequestHeader(name = "Authorization") String token);

}
