package com.cognizant.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.cognizant.service.model.Message;

@FeignClient(name = "product-microservice", url = "http://localhost:8003/product")
public interface ProductClient {

	@GetMapping("/{id}")
	Message getProductById(@RequestHeader(name = "Authorization") String token, @PathVariable Long id);
	
	@GetMapping("/my-products")
	Message getMyProducts(@RequestHeader(name = "Authorization")String token);
	

}
