package com.cognizant.service.dto;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.cognizant.service.model.ServiceType;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DTOTest {
	
	AppProduct product = new AppProduct();
	ValidatingDTO validatingDTO = new ValidatingDTO();
	Users users = new Users();
	ServiceRequestDTO serviceRequestDTO = new ServiceRequestDTO();
	AppServiceReqReportDTO appServiceReqReportDTO = new AppServiceReqReportDTO();
	
	@Test
	void testAppProduct() {
		product.setId(1);
		product.setMake("Test");
		product.setProductImageUrl("testUrl");
		product.setModel("Test Model");
		product.setCreatedBy("Zaid Khan");
		product.setCost(3000);
		
		assertEquals(product.getCost(), 3000);
		assertEquals(product.getMake(), "Test");
		assertEquals(product.getProductImageUrl(),"testUrl");
		assertEquals(product.getModel(), "Test Model");
		assertEquals(product.getCreatedBy(), "Zaid Khan");
	}
	
	@Test
	void testValidatingDTO() {
		validatingDTO.setEmail("test@test.com");
		validatingDTO.setUserRole("ROLE_USER");
		validatingDTO.setValidStatus(true);
		
		assertTrue(validatingDTO.isValidStatus());
		assertEquals(validatingDTO.getEmail(), "test@test.com");
		assertEquals(validatingDTO.getUserRole(), "ROLE_USER");
	}
	
	@Test
	void testUsers() {
		users.setActive(true);
		users.setEmail("test@test.com");
		users.setGender("Female");
		users.setName("Tester test");
		users.setMobile(1234567890);
		users.setRole("ROLE_ADMIN");
		
		assertTrue(users.isActive());
		assertEquals(users.getEmail(), "test@test.com");
		assertEquals(users.getMobile(), 1234567890);
		assertEquals(users.getGender(), "Female");
		assertEquals(users.getRole(), "ROLE_ADMIN");
		assertEquals(users.getName(), "Tester test");
	}
	
	@Test
	void testServiceRequestDTO() {
		serviceRequestDTO.setDescription("lorem ipsum");
		serviceRequestDTO.setProblem("problem lorem");
		serviceRequestDTO.setProductId(1);
		
		assertEquals(serviceRequestDTO.getDescription(), "lorem ipsum");
		assertEquals(serviceRequestDTO.getProblem(), "problem lorem");
		assertEquals(serviceRequestDTO.getProductId(), 1);
	}
	
	@Test
	void testAppServiceReqReportDTO() {
		appServiceReqReportDTO.setActionTaken("TEST");
		appServiceReqReportDTO.setDiagnosisDetails("Diagnosis");
		appServiceReqReportDTO.setPaid(true);
		appServiceReqReportDTO.setRepairDetails("RepairDetails");
		appServiceReqReportDTO.setServiceType(ServiceType.REPAIR);
		appServiceReqReportDTO.setVisitFees(4000);
		
		assertTrue(appServiceReqReportDTO.isPaid());
		assertEquals(appServiceReqReportDTO.getActionTaken(), "TEST");
		assertEquals(appServiceReqReportDTO.getDiagnosisDetails(), "Diagnosis");
		assertEquals(appServiceReqReportDTO.getRepairDetails(), "RepairDetails");
		assertEquals(appServiceReqReportDTO.getVisitFees(), 4000);
		assertEquals(appServiceReqReportDTO.getServiceType(), ServiceType.REPAIR);
	}
}
