package com.cognizant.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableFeignClients
@EnableEurekaClient
@EnableSwagger2
@SpringBootApplication
public class ServiceMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceMicroserviceApplication.class, args);
	}

}
