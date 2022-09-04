package com.cognizant.service.dto;

import com.cognizant.service.model.ServiceType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public @Data @AllArgsConstructor @NoArgsConstructor class AppServiceReqReportDTO {

	private long serviceReqId;
	private ServiceType serviceType;
	private String actionTaken;
	private String diagnosisDetails;
	private boolean paid;
	private double visitFees;
	private String repairDetails;
	
}
