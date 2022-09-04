package com.cognizant.service.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
public @Data @AllArgsConstructor @NoArgsConstructor class AppServiceReqReport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private long serviceReqId;
	private Date reportDate;
	private ServiceType serviceType;
	private String actionTaken;
	private String diagnosisDetails;
	private boolean paid;
	private double visitFees;
	private String repairDetails;

	public AppServiceReqReport(long serviceReqId, ServiceType serviceType, String actionTaken, String diagnosisDetails,
			boolean paid, double visitFees, String repairDetails) {
		super();
		this.serviceReqId = serviceReqId;
		this.serviceType = serviceType;
		this.reportDate = new Date();
		this.actionTaken = actionTaken;
		this.diagnosisDetails = diagnosisDetails;
		this.paid = paid;
		this.visitFees = visitFees;
		this.repairDetails = repairDetails;
	}

}
