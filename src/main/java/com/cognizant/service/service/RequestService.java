package com.cognizant.service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cognizant.service.dto.AppServiceReqReportDTO;
import com.cognizant.service.dto.ServiceRequestDTO;
import com.cognizant.service.exception.InvalidDataAccessException;
import com.cognizant.service.exception.NoRequestFoundException;
import com.cognizant.service.exception.RequestNotExistsException;
import com.cognizant.service.exception.ServiceAlreadyProvidedException;
import com.cognizant.service.exception.UnauthorizedAccessException;
import com.cognizant.service.model.AppServiceReqReport;
import com.cognizant.service.model.ServiceRequest;
import com.cognizant.service.model.ServiceResponse;

@Service
public interface RequestService {

	ServiceResponse newServiceRequest(String token, ServiceRequestDTO dto) throws UnauthorizedAccessException;

	List<ServiceResponse> getMyProductRequests(String token) throws NoRequestFoundException;

	List<ServiceRequest> getAllRequests(String token) throws InvalidDataAccessException;

	List<ServiceRequest> getMyRequest(String token) throws InvalidDataAccessException, NoRequestFoundException;

	ServiceRequest deleteRequest(String token, long id)
			throws InvalidDataAccessException, RequestNotExistsException, NoRequestFoundException;

	ServiceRequest updateRequest(String token, long id, ServiceRequestDTO requestDTO)
			throws InvalidDataAccessException, RequestNotExistsException, NoRequestFoundException;

	List<ServiceRequest> getRequestAsPerUserId(String token, long userId) throws InvalidDataAccessException;

	AppServiceReqReport createNewReqReport(String token, AppServiceReqReportDTO requestDTO)
			throws InvalidDataAccessException, NoRequestFoundException, ServiceAlreadyProvidedException;

	List<AppServiceReqReport> getReportByUserId(String token, long userId) throws InvalidDataAccessException;

	List<AppServiceReqReport> getAllReport(String token) throws InvalidDataAccessException, NoRequestFoundException;

	AppServiceReqReport getByReportId(String token, long id) throws InvalidDataAccessException, NoRequestFoundException;

	AppServiceReqReport getReportByRequestId(String token, long id) throws InvalidDataAccessException,
			NoRequestFoundException, UnauthorizedAccessException, RequestNotExistsException;

	

}
