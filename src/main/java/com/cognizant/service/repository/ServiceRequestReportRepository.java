package com.cognizant.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.service.model.AppServiceReqReport;

public interface ServiceRequestReportRepository extends JpaRepository<AppServiceReqReport, Long> {
	
	List<AppServiceReqReport> findByPaid(boolean isPaid);
	Optional<AppServiceReqReport> findByServiceReqId(long serviceReqId);
	Optional<AppServiceReqReport> findByIdAndServiceReqId(long id, long serviceReqId);
	
}
