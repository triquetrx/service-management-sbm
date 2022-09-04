package com.cognizant.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cognizant.service.model.ServiceRequest;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

	List<ServiceRequest> findByProductId(long productId);
	List<ServiceRequest> findByUserId(long userId);
	List<ServiceRequest> findByUserIdAndProductId(long userId,long productId);
}
