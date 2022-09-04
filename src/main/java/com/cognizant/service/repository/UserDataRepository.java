package com.cognizant.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.service.model.UserData;

public interface UserDataRepository extends JpaRepository<UserData, Long> {
	
}
