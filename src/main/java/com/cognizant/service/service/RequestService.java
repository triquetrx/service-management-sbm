package com.cognizant.service.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cognizant.service.client.AuthClient;
import com.cognizant.service.client.ProductClient;
import com.cognizant.service.client.UserClient;
import com.cognizant.service.dto.AppProduct;
import com.cognizant.service.dto.AppServiceReqReportDTO;
import com.cognizant.service.dto.ServiceRequestDTO;
import com.cognizant.service.dto.Users;
import com.cognizant.service.dto.ValidatingDTO;
import com.cognizant.service.exception.InvalidDataAccessException;
import com.cognizant.service.exception.NoRequestFoundException;
import com.cognizant.service.exception.RequestNotExistsException;
import com.cognizant.service.exception.ServiceAlreadyProvidedException;
import com.cognizant.service.exception.UnauthorizedAccessException;
import com.cognizant.service.model.AppServiceReqReport;
import com.cognizant.service.model.Message;
import com.cognizant.service.model.ServiceRequest;
import com.cognizant.service.model.ServiceResponse;
import com.cognizant.service.model.ServiceStatus;
import com.cognizant.service.model.UserData;
import com.cognizant.service.repository.ServiceRequestReportRepository;
import com.cognizant.service.repository.ServiceRequestRepository;
import com.cognizant.service.repository.UserDataRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Service
public class RequestService {

	@Autowired
	ServiceRequestRepository serviceRequestRepository;

	@Autowired
	UserDataRepository userRepository;

	@Autowired
	AuthClient authClient;

	@Autowired
	ProductClient productClient;

	@Autowired
	UserClient userClient;

	@Autowired
	ServiceRequestReportRepository reportRepository;

	@Transactional
	public ServiceResponse newServiceRequest(String token, ServiceRequestDTO dto) throws UnauthorizedAccessException {
		ValidatingDTO validator = authClient.validatingToken(token);
		// Check if jwt token is valid and if the user is not a admin
		if (validator.isValidStatus() && !validator.getUserRole().equalsIgnoreCase("ROLE_ADMIN")) {
			Message product = productClient.getProductById(token, dto.getProductId());
			// Check if product exists
			if (product.getStatus() == 200) {
				Gson gson = new GsonBuilder().create();
				Users users = gson.fromJson(gson.toJson(userClient.getCurrentUserDetails(token).getPayload()),
						Users.class);
				Optional<UserData> user = userRepository.findById(users.getId());
				// Create local memory of user data for later usage i.e. contact them etc
				// If the user does not exists in local memory create else use existing
				if (user.isEmpty()) {
					UserData userSave = userRepository
							.save(new UserData(users.getId(), users.getName(), users.getEmail(), users.getMobile()));
					user = userRepository.findById(userSave.getUserId());
				}
				// Save the request in the database
				ServiceRequest save = serviceRequestRepository
						.save(new ServiceRequest(dto.getProductId(), user.get().getUserId(), new Date(),
								dto.getProblem(), dto.getDescription(), ServiceStatus.Pending));

				return new ServiceResponse(save.getId(), dto.getProductId(), user.get(), save.getRequestDate(),
						save.getProblem(), save.getDescription(), save.getStatus());
			}

		}
		throw new UnauthorizedAccessException("UNAUTHORIZED_ACCESS");

	}

	@Transactional
	public List<ServiceResponse> getMyProductRequests(String token) {
		Gson gson = new GsonBuilder().create();
		AppProduct[] appProduct = gson.fromJson(gson.toJson(productClient.getMyProducts(token).getPayload()),
				AppProduct[].class);
		List<List<ServiceResponse>> result = Arrays.asList(appProduct).parallelStream()
				.map(ele -> serviceRequestRepository.findByProductId(ele.getId()))
				.map(ele -> ele.parallelStream()
						.map(item -> new ServiceResponse(item.getId(), item.getProductId(),
								userRepository.findById(item.getUserId()).get(), item.getRequestDate(),
								item.getProblem(), item.getDescription(), item.getStatus()))
						.collect(Collectors.toList()))
				.collect(Collectors.toList());

		return result.get(0);
	}

	@Transactional
	public List<ServiceRequest> getAllRequests(String token) throws InvalidDataAccessException {
		ValidatingDTO validator = authClient.validatingToken(token);
		if (validator.isValidStatus() && validator.getUserRole().equalsIgnoreCase("ROLE_ADMIN")) {
			return serviceRequestRepository.findAll();
		}
		throw new InvalidDataAccessException("UNAUTHORIZED_DATA_ACCESS");
	}

	@Transactional
	public List<ServiceRequest> getMyRequest(String token) throws InvalidDataAccessException {
		if (authClient.validatingToken(token).isValidStatus()) {
			Gson gson = new GsonBuilder().create();
			Users users = gson.fromJson(gson.toJson(userClient.getCurrentUserDetails(token).getPayload()), Users.class);
			return serviceRequestRepository.findByUserId(users.getId());
		}
		throw new InvalidDataAccessException("UNAUTHORIZED_DATA_ACCESS");
	}

	@Transactional
	public ServiceRequest deleteRequest(String token, long id)
			throws InvalidDataAccessException, RequestNotExistsException {
		if (authClient.validatingToken(token).getUserRole().equalsIgnoreCase("ROE_ADMIN")) {
			ServiceRequest serviceRequest = serviceRequestRepository.findById(id).get();
			serviceRequestRepository.delete(serviceRequest);
			return serviceRequest;
		}
		List<ServiceRequest> requests = getMyRequest(token).parallelStream().filter(ele -> ele.getId() == id)
				.collect(Collectors.toList());
		if (requests.isEmpty()) {
			throw new RequestNotExistsException("ITEM REQUESTED TO DELETE IS INVALID");
		}
		serviceRequestRepository.delete(requests.get(0));
		return requests.get(0);
	}

	@Transactional
	public ServiceRequest updateRequest(String token, long id, ServiceRequestDTO requestDTO)
			throws InvalidDataAccessException, RequestNotExistsException {
		if (authClient.validatingToken(token).getUserRole().equalsIgnoreCase("ROE_ADMIN")) {
			ServiceRequest serviceRequest = serviceRequestRepository.findById(id).get();
			serviceRequest.setDescription(requestDTO.getDescription());
			serviceRequest.setProblem(requestDTO.getProblem());
			return serviceRequestRepository.save(serviceRequest);
		}
		List<ServiceRequest> myRequests = getMyRequest(token);
		for (ServiceRequest myRequest : myRequests) {
			if (myRequest.getId() == id) {
				myRequest.setDescription(requestDTO.getDescription());
				myRequest.setProblem(requestDTO.getProblem());
				return serviceRequestRepository.save(myRequest);
			}
		}
		throw new RequestNotExistsException("ITEM REQUESTED TO UPDATE IS INVALID");
	}

	@Transactional
	public List<ServiceRequest> getRequestAsPerUserId(String token, long userId) throws InvalidDataAccessException {
		ValidatingDTO validator = authClient.validatingToken(token);
		if (validator.isValidStatus()) {
			if (validator.getUserRole().equalsIgnoreCase("ROLE_ADMIN")) {
				return serviceRequestRepository.findByUserId(userId);
			}
			Gson gson = new GsonBuilder().create();
			AppProduct[] appProduct = gson.fromJson(gson.toJson(productClient.getMyProducts(token).getPayload()),
					AppProduct[].class);
			return Arrays.asList(appProduct).parallelStream()
					.map(ele -> serviceRequestRepository.findByUserIdAndProductId(userId, ele.getId()))
					.collect(Collectors.toList()).get(0);
		}
		throw new InvalidDataAccessException("INVALID DATA ACCESS");
	}

	@Transactional
	public AppServiceReqReport createNewReqReport(String token, AppServiceReqReportDTO requestDTO)
			throws InvalidDataAccessException, NoRequestFoundException, ServiceAlreadyProvidedException {
		ValidatingDTO validator = authClient.validatingToken(token);
		if (validator.isValidStatus()) {
			List<ServiceResponse> myProductRequests = getMyProductRequests(token);
			if (myProductRequests.isEmpty()) {
				throw new NoRequestFoundException("No request with the mentioned request id found");
			}
			Optional<AppServiceReqReport> findByServiceReqId = reportRepository
					.findByServiceReqId(requestDTO.getServiceReqId());
			if (findByServiceReqId.isPresent()) {
				throw new ServiceAlreadyProvidedException("Service already provided for the mentioned request id");
			}
			Optional<ServiceResponse> filter = getMyProductRequests(token).parallelStream()
					.filter(ele -> ele.getId() == requestDTO.getServiceReqId()).findFirst();
			if (filter.isEmpty()) {
				throw new NoRequestFoundException("No request with the mentioned request id found");
			}
			AppServiceReqReport save = reportRepository.save(new AppServiceReqReport(requestDTO.getServiceReqId(),
					requestDTO.getServiceType(), requestDTO.getActionTaken(), requestDTO.getDiagnosisDetails(),
					requestDTO.isPaid(), requestDTO.getVisitFees(), requestDTO.getRepairDetails()));
			ServiceRequest serviceRequest = serviceRequestRepository.findById(filter.get().getId()).get();
			serviceRequest.setStatus(ServiceStatus.Resolved);
			serviceRequestRepository.save(serviceRequest);
			return save;
		}
		throw new InvalidDataAccessException("INVALID DATA ACCESS");
	}

	@Transactional
	public List<AppServiceReqReport> getReportByUserId(String token, long userId) throws InvalidDataAccessException {
		List<ServiceRequest> myRequests = getRequestAsPerUserId(token, userId);
		List<AppServiceReqReport> result = new ArrayList<>();
		for (ServiceRequest myRequest : myRequests) {
			result.add(reportRepository.findByServiceReqId(myRequest.getId()).get());
		}
		return result;
	}

	@Transactional
	public List<AppServiceReqReport> getAllReport(String token) throws InvalidDataAccessException {
		ValidatingDTO validator = authClient.validatingToken(token);
		if (validator.isValidStatus()) {
			if (validator.getUserRole().equalsIgnoreCase("ROLE_ADMIN")) {
				return reportRepository.findAll();
			}
			return getMyProductRequests(token).parallelStream()
					.map(ele -> reportRepository.findByServiceReqId(ele.getId()).get()).collect(Collectors.toList());
		}
		throw new InvalidDataAccessException("INVALID DATA ACCESS");
	}

	//Parallel Stream pending
	@Transactional
	public AppServiceReqReport getByReportId(String token, long id) throws InvalidDataAccessException {
		ValidatingDTO validator = authClient.validatingToken(token);
		if (validator.isValidStatus()) {
			if (validator.getUserRole().equalsIgnoreCase("ROLE_ADMIN")) {
				return reportRepository.findById(id).get();
			}
			List<ServiceResponse> myProductRequests = getMyProductRequests(token);
			for (ServiceResponse myProductRequest : myProductRequests) {
				Optional<AppServiceReqReport> dummyRes = reportRepository.findByIdAndServiceReqId(id,
						myProductRequest.getId());
				if (dummyRes.isPresent()) {
					return dummyRes.get();
				}
			}
		}
		throw new InvalidDataAccessException("INVALID DATA ACCESS");
	}

}
