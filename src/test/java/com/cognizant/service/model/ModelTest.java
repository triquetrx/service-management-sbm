package com.cognizant.service.model;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@RunWith(SpringRunner.class)
public class ModelTest {
	
	Message message = new Message();
	AppServiceReqReport appServiceReqReport = new AppServiceReqReport();
	ServiceRequest serviceRequest = new ServiceRequest();
	UserData users = new UserData();
	ServiceResponse serviceResponse = new ServiceResponse();
	
	
	@Test
	void testMessage() {
		message.setMessage("This is a test");
		message.setPayload("Test");
		message.setStatus(200);
		
		assertEquals(message.getMessage(), "This is a test");
		assertEquals(message.getPayload(), "Test");
		assertEquals(message.getStatus(), 200);
	}
	
	@Test
	void testServiceRequestDTO() {
		serviceRequest.setId(1);
		serviceRequest.setUserId(2);
		serviceRequest.setDescription("lorem ipsum");
		serviceRequest.setProblem("problem lorem");
		serviceRequest.setProductId(1);
		serviceRequest.setStatus(ServiceStatus.Pending);
		
		assertEquals(serviceRequest.getStatus(), ServiceStatus.Pending);
		assertEquals(serviceRequest.getDescription(), "lorem ipsum");
		assertEquals(serviceRequest.getProblem(), "problem lorem");
		assertEquals(serviceRequest.getProductId(), 1);
		assertEquals(serviceRequest.getId(), 1);
		assertEquals(serviceRequest.getUserId(), 2);
	}
	
	@Test
	void testAppServiceReqReportDTO() {
		appServiceReqReport.setId(1);
		appServiceReqReport.setActionTaken("TEST");
		appServiceReqReport.setDiagnosisDetails("Diagnosis");
		appServiceReqReport.setPaid(true);
		appServiceReqReport.setRepairDetails("RepairDetails");
		appServiceReqReport.setServiceType(ServiceType.REPAIR);
		appServiceReqReport.setVisitFees(4000);
		
		assertTrue(appServiceReqReport.isPaid());
		assertEquals(appServiceReqReport.getId(), 1);
		assertEquals(appServiceReqReport.getActionTaken(), "TEST");
		assertEquals(appServiceReqReport.getDiagnosisDetails(), "Diagnosis");
		assertEquals(appServiceReqReport.getRepairDetails(), "RepairDetails");
		assertEquals(appServiceReqReport.getVisitFees(), 4000);
		assertEquals(appServiceReqReport.getServiceType(), ServiceType.REPAIR);
	}
	
	@Test
	void testUsers() {
		users.setEmail("test@test.com");
		users.setName("Tester test");
		users.setMobile(1234567890);
		
		assertEquals(users.getEmail(), "test@test.com");
		assertEquals(users.getMobile(), 1234567890);
		assertEquals(users.getName(), "Tester test");
	}
	
	@Test
	void testServiceResponse() {
		serviceResponse.setDescription("Description");
		serviceResponse.setId(1);
		serviceResponse.setProblem("Problem");
		serviceResponse.setProductId(2);
		users.setName("Zaid");
		users.setEmail("test@test.com");
		serviceResponse.setUser(users);
		serviceResponse.setStatus(ServiceStatus.Pending);
		
		assertEquals(serviceResponse.getDescription(), "Description");
		assertEquals(serviceResponse.getProblem(), "Problem");
		assertEquals(serviceResponse.getUser().getName(), "Zaid");
		assertEquals(serviceResponse.getUser().getEmail(), "test@test.com");
		assertEquals(serviceResponse.getId(), 1);
		assertEquals(serviceResponse.getProductId(), 2);
		assertEquals(serviceResponse.getStatus(), ServiceStatus.Pending);
	}

}
