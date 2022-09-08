package com.cognizant.service.service.impl;

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
import com.cognizant.service.service.RequestService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Service
public class RequestServiceImpl implements RequestService {
	
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

	@Override
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

	@Override
	@Transactional
	public List<ServiceResponse> getMyProductRequests(String token) throws NoRequestFoundException {
		Gson gson = new GsonBuilder().create();
		AppProduct[] appProduct = gson.fromJson(gson.toJson(productClient.getMyProducts(token).getPayload()),
				AppProduct[].class);
		List<ServiceResponse> result = new ArrayList<>();
		if (appProduct != null) {
			Arrays.asList(appProduct).parallelStream().map(ele -> serviceRequestRepository.findByProductId(ele.getId()))
					.map(ele -> ele.parallelStream()
							.map(item -> new ServiceResponse(item.getId(), item.getProductId(),
									userRepository.findById(item.getUserId()).get(), item.getRequestDate(),
									item.getProblem(), item.getDescription(), item.getStatus()))
							.collect(Collectors.toList()))
					.collect(Collectors.toList()).parallelStream().filter(ele -> !ele.isEmpty())
					.map(ele -> result.addAll(ele)).collect(Collectors.toList());
			if (result.isEmpty()) {
				throw new NoRequestFoundException("No data found");
			}
			return result;
		}
		throw new NoRequestFoundException("No product found");
	}

	@Override
	@Transactional
	public List<ServiceRequest> getAllRequests(String token) throws InvalidDataAccessException {
		ValidatingDTO validator = authClient.validatingToken(token);
		if (validator.isValidStatus() && validator.getUserRole().equalsIgnoreCase("ROLE_ADMIN")) {
			return serviceRequestRepository.findAll();
		}
		throw new InvalidDataAccessException("UNAUTHORIZED_DATA_ACCESS");
	}

	@Override
	@Transactional
	public List<ServiceRequest> getMyRequest(String token) throws InvalidDataAccessException, NoRequestFoundException {
		if (authClient.validatingToken(token).isValidStatus()) {
			Gson gson = new GsonBuilder().create();
			Users users = gson.fromJson(gson.toJson(userClient.getCurrentUserDetails(token).getPayload()), Users.class);
			List<ServiceRequest> result = serviceRequestRepository.findByUserId(users.getId());
			if (result.isEmpty()) {
				throw new NoRequestFoundException("No requests Raised");
			}
			return result;
		}
		throw new InvalidDataAccessException("UNAUTHORIZED_DATA_ACCESS");
	}

	@Override
	@Transactional
	public ServiceRequest deleteRequest(String token, long id)
			throws InvalidDataAccessException, RequestNotExistsException, NoRequestFoundException {
		if (authClient.validatingToken(token).getUserRole().equalsIgnoreCase("ROLE_ADMIN")) {
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

	@Override
	@Transactional
	public ServiceRequest updateRequest(String token, long id, ServiceRequestDTO requestDTO)
			throws InvalidDataAccessException, RequestNotExistsException, NoRequestFoundException {
		if (authClient.validatingToken(token).getUserRole().equalsIgnoreCase("ROLE_ADMIN")) {
			ServiceRequest serviceRequest = serviceRequestRepository.findById(id).get();
			serviceRequest.setDescription(requestDTO.getDescription());
			serviceRequest.setProblem(requestDTO.getProblem());
			return serviceRequestRepository.save(serviceRequest);
		}
		Optional<ServiceRequest> myRequest = getMyRequest(token).parallelStream().filter(ele -> ele.getId() == id)
				.findFirst();
		if (myRequest.isPresent()) {
			myRequest.get().setDescription(requestDTO.getDescription());
			myRequest.get().setProblem(requestDTO.getProblem());
			return serviceRequestRepository.save(myRequest.get());
		}
		throw new RequestNotExistsException("ITEM REQUESTED TO UPDATE IS INVALID");
	}

	@Override
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
			if (appProduct != null) {
				List<ServiceRequest> result = new ArrayList<>();
				Arrays.asList(appProduct).parallelStream()
						.map(ele -> serviceRequestRepository.findByUserIdAndProductId(userId, ele.getId()))
						.map(ele -> result.addAll(ele));
				return result;
			}
		}
		throw new InvalidDataAccessException("INVALID DATA ACCESS");
	}

	@Override
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

	@Override
	@Transactional
	public List<AppServiceReqReport> getReportByUserId(String token, long userId) throws InvalidDataAccessException {
		List<ServiceRequest> myRequests = getRequestAsPerUserId(token, userId);
		List<AppServiceReqReport> result = new ArrayList<>();
		for (ServiceRequest myRequest : myRequests) {
			result.add(reportRepository.findByServiceReqId(myRequest.getId()).get());
		}
		return result;
	}

	@Override
	@Transactional
	public List<AppServiceReqReport> getAllReport(String token)
			throws InvalidDataAccessException, NoRequestFoundException {
		ValidatingDTO validator = authClient.validatingToken(token);
		if (validator.isValidStatus()) {
			if (validator.getUserRole().equalsIgnoreCase("ROLE_ADMIN")) {
				return reportRepository.findAll();
			}
			List<AppServiceReqReport> result = new ArrayList<>();
			try {
				getMyProductRequests(token).stream().map(ele -> reportRepository.findByServiceReqId(ele.getId()))
						.filter(ele -> ele.isPresent()).map(ele -> result.add(ele.get())).collect(Collectors.toList());
			} finally {
				if (result.isEmpty()) {
					throw new NoRequestFoundException("No Data found");
				}
			}
			return result;
		}
		throw new InvalidDataAccessException("INVALID DATA ACCESS");
	}

	@Override
	@Transactional
	public AppServiceReqReport getByReportId(String token, long id)
			throws InvalidDataAccessException, NoRequestFoundException {
		ValidatingDTO validator = authClient.validatingToken(token);
		if (validator.isValidStatus()) {
			if (validator.getUserRole().equalsIgnoreCase("ROLE_ADMIN")) {
				return reportRepository.findById(id).get();
			} else {
				List<ServiceResponse> myProductRequests = getMyProductRequests(token);
				if (myProductRequests.isEmpty()) {
					return null;
				}
				for (ServiceResponse myProductRequest : myProductRequests) {
					Optional<AppServiceReqReport> dummyRes = reportRepository.findByIdAndServiceReqId(id,
							myProductRequest.getId());
					if (dummyRes.isPresent()) {
						return dummyRes.get();
					}
				}
			}
		}
		throw new InvalidDataAccessException("INVALID DATA ACCESS");
	}

	@Override
	@Transactional
	public AppServiceReqReport getReportByRequestId(String token, long id) throws InvalidDataAccessException,
			NoRequestFoundException, UnauthorizedAccessException, RequestNotExistsException {
		ValidatingDTO validator = authClient.validatingToken(token);
		if (validator.isValidStatus()) {
			if (validator.getUserRole().equalsIgnoreCase("ROLE_ADMIN")) {
				Optional<AppServiceReqReport> resultAdmin = reportRepository.findByServiceReqId(id);
				if (resultAdmin.isPresent()) {
					return resultAdmin.get();
				}
				throw new RequestNotExistsException("Invalid id searched");
			}
			Optional<ServiceRequest> checkifRequestWasMine = getMyRequest(token).parallelStream()
					.filter(ele -> ele.getId() == id).findFirst();
			AppServiceReqReport result = null;
			if (checkifRequestWasMine.isPresent()) {
				result = reportRepository.findByServiceReqId(id).get();
			} else {
				Optional<AppServiceReqReport> collect = getAllReport(token).parallelStream()
						.filter(ele -> ele.getServiceReqId() == id).findFirst();
				if (collect.isPresent()) {
					result = reportRepository.findByServiceReqId(id).get();
				}
			}
			if(result != null) {				
				return result;
			}
			throw new UnauthorizedAccessException("Unauthorized access of data");
		}
		throw new InvalidDataAccessException("INVALID DATA ACCESS");
	}

}
