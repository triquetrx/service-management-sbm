package com.cognizant.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.service.dto.AppServiceReqReportDTO;
import com.cognizant.service.dto.ServiceRequestDTO;
import com.cognizant.service.exception.InvalidDataAccessException;
import com.cognizant.service.exception.NoRequestFoundException;
import com.cognizant.service.exception.RequestNotExistsException;
import com.cognizant.service.exception.ServiceAlreadyProvidedException;
import com.cognizant.service.exception.UnauthorizedAccessException;
import com.cognizant.service.model.AppServiceReqReport;
import com.cognizant.service.model.Message;
import com.cognizant.service.model.ServiceRequest;
import com.cognizant.service.model.ServiceResponse;
import com.cognizant.service.service.RequestService;

import feign.FeignException.FeignClientException;

@RestController
@RequestMapping("/servicereq")
@CrossOrigin(origins = "http://localhost:5000")
public class ServicesController {

	@Autowired
	RequestService service;

	@PostMapping
	public ResponseEntity<?> createServiceBooking(@RequestHeader(name = "Authorization") String token,
			@RequestBody ServiceRequestDTO request) {
		try {
			ServiceResponse response = service.newServiceRequest(token, request);
			return new ResponseEntity<>(new Message(200, "New request raised with id:"+response.getId(), response), HttpStatus.OK);
		} catch (UnauthorizedAccessException e) {
			return new ResponseEntity<>(new Message(401, "AUTHORIZATION_ERROR", null), HttpStatus.UNAUTHORIZED);
		} catch (FeignClientException e) {
			String[] message = e.getMessage().split(" ");
			int errCode = Integer.parseInt(message[0].split("")[1] + message[0].split("")[2] + message[0].split("")[3]);
			return new ResponseEntity<>(new Message(errCode, "AUTHORIZATION_ERROR", message[5]),
					HttpStatus.valueOf(errCode));
		}
	}

	@GetMapping
	public ResponseEntity<?> getMyProductServices(@RequestHeader(name = "Authorization") String token) {
		try {
			List<ServiceResponse> myProductRequests = service.getMyProductRequests(token);
			return new ResponseEntity<>(new Message(200, "Data Found", myProductRequests), HttpStatus.OK);
		} catch (NoRequestFoundException e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<>(new Message(404, e.getMessage(), null), HttpStatus.NOT_FOUND);
		} catch (FeignClientException e) {
			String[] message = e.getMessage().split(" ");
			int errCode = Integer.parseInt(message[0].split("")[1] + message[0].split("")[2] + message[0].split("")[3]);
			return new ResponseEntity<>(new Message(errCode, "AUTHORIZATION_ERROR", message[5]),
					HttpStatus.valueOf(errCode));
		}
	}

	@GetMapping("/my-requests")
	public ResponseEntity<?> getMyRequests(@RequestHeader(name = "Authorization") String token) {
		try {
			List<ServiceRequest> myRequests = service.getMyRequest(token);
			if (myRequests.isEmpty()) {
				return new ResponseEntity<>(new Message(200, "No Requests Raised", myRequests), HttpStatus.OK);
			}
			return new ResponseEntity<>(new Message(200, "DATA_FOUND", myRequests), HttpStatus.OK);
		} catch (NoRequestFoundException e) {
			return new ResponseEntity<>(new Message(404, e.getMessage(), null), HttpStatus.UNAUTHORIZED);
		} catch (InvalidDataAccessException e) {
			return new ResponseEntity<>(new Message(401, e.getMessage(), null), HttpStatus.UNAUTHORIZED);
		} catch (FeignClientException e) {
			String[] message = e.getMessage().split(" ");
			int errCode = Integer.parseInt(message[0].split("")[1] + message[0].split("")[2] + message[0].split("")[3]);
			return new ResponseEntity<>(new Message(errCode, "AUTHORIZATION_ERROR", message[5]),
					HttpStatus.valueOf(errCode));
		} 
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteRequest(@RequestHeader(name = "Authorization") String token, @PathVariable long id) {
		try {
			ServiceRequest response = service.deleteRequest(token, id);
			return new ResponseEntity<>(new Message(200, "Item with id: " + id + " DELETED", response), HttpStatus.OK);
		} catch (InvalidDataAccessException e) {
			return new ResponseEntity<>(new Message(401, e.getMessage(), null), HttpStatus.UNAUTHORIZED);
		} catch (RequestNotExistsException e) {
			return new ResponseEntity<>(new Message(404, e.getMessage(), null), HttpStatus.NOT_FOUND);
		} catch (NoRequestFoundException e) {
			return new ResponseEntity<>(new Message(404, e.getMessage(), null), HttpStatus.NOT_FOUND);
		} catch (FeignClientException e) {
			String[] message = e.getMessage().split(" ");
			int errCode = Integer.parseInt(message[0].split("")[1] + message[0].split("")[2] + message[0].split("")[3]);
			return new ResponseEntity<>(new Message(errCode, "AUTHORIZATION_ERROR", message[5]),
					HttpStatus.valueOf(errCode));
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateRequest(@RequestHeader(name = "Authorization") String token, @PathVariable long id,
			@RequestBody ServiceRequestDTO serviceRequestDTO) {
		try {
			ServiceRequest response = service.updateRequest(token, id, serviceRequestDTO);
			return new ResponseEntity<>(new Message(200, "Request with id: " + id + " updated", response), HttpStatus.OK);
		} catch (InvalidDataAccessException e) {
			return new ResponseEntity<>(new Message(401, e.getMessage(), null), HttpStatus.UNAUTHORIZED);
		} catch (RequestNotExistsException e) {
			return new ResponseEntity<>(new Message(401, e.getMessage(), null), HttpStatus.NOT_FOUND);
		} catch (NoRequestFoundException e) {
			return new ResponseEntity<>(new Message(404, e.getMessage(), null), HttpStatus.NOT_FOUND);
		} catch (FeignClientException e) {
			String[] message = e.getMessage().split(" ");
			int errCode = Integer.parseInt(message[0].split("")[1] + message[0].split("")[2] + message[0].split("")[3]);
			return new ResponseEntity<>(new Message(errCode, "AUTHORIZATION_ERROR", message[5]),
					HttpStatus.valueOf(errCode));
		}
	}

	@GetMapping("/{userId}")
	public ResponseEntity<?> getRequestAsPerUserId(@RequestHeader(name = "Authorization") String token,
			@PathVariable long userId) {
		try {
			List<ServiceRequest> response = service.getRequestAsPerUserId(token, userId);
			if (response.isEmpty()) {
				return new ResponseEntity<>(new Message(200, "No Requests found for user with id: " + userId, response),
						HttpStatus.OK);
			}
			return new ResponseEntity<>(new Message(200, "USER'S REQUEST WITH ID " + userId, response), HttpStatus.OK);
		} catch (InvalidDataAccessException e) {
			return new ResponseEntity<>(new Message(401, e.getMessage(), null), HttpStatus.UNAUTHORIZED);
		} catch (FeignClientException e) {
			String[] message = e.getMessage().split(" ");
			int errCode = Integer.parseInt(message[0].split("")[1] + message[0].split("")[2] + message[0].split("")[3]);
			return new ResponseEntity<>(new Message(errCode, "AUTHORIZATION_ERROR", message[5]),
					HttpStatus.valueOf(errCode));
		}
	}

	@PostMapping("/report")
	public ResponseEntity<?> createNewServiceRequest(@RequestHeader(name = "Authorization") String token,
			@RequestBody AppServiceReqReportDTO reportDTO) {
		try {
			AppServiceReqReport reqReport = service.createNewReqReport(token, reportDTO);
			return new ResponseEntity<>(new Message(200, "Report Created successfully", reqReport), HttpStatus.OK);
		} catch (InvalidDataAccessException e) {
			return new ResponseEntity<>(new Message(401, e.getMessage(), null), HttpStatus.UNAUTHORIZED);
		} catch (NoRequestFoundException e) {
			return new ResponseEntity<>(new Message(404, e.getMessage(), null), HttpStatus.NOT_FOUND);
		} catch (ServiceAlreadyProvidedException e) {
			return new ResponseEntity<>(new Message(400, e.getMessage(), null), HttpStatus.BAD_REQUEST);
		} catch (FeignClientException e) {
			String[] message = e.getMessage().split(" ");
			int errCode = Integer.parseInt(message[0].split("")[1] + message[0].split("")[2] + message[0].split("")[3]);
			return new ResponseEntity<>(new Message(errCode, "AUTHORIZATION_ERROR", message[5]),
					HttpStatus.valueOf(errCode));
		}
	}

	@GetMapping("/report/userId/{userId}")
	public ResponseEntity<?> getReportForAUser(@RequestHeader(name = "Authorization") String token,
			@PathVariable long userId) {
		try {
			List<ServiceRequest> response = service.getRequestAsPerUserId(token, userId);
			if (response.isEmpty()) {
				return new ResponseEntity<>(new Message(200, "NO DATA FOUND", null), HttpStatus.OK);
			}
			return new ResponseEntity<>(new Message(200, "DATA FOUND", response), HttpStatus.OK);
		} catch (InvalidDataAccessException e) {
			return new ResponseEntity<>(new Message(401, e.getMessage(), null), HttpStatus.UNAUTHORIZED);
		} catch (FeignClientException e) {
			String[] message = e.getMessage().split(" ");
			int errCode = Integer.parseInt(message[0].split("")[1] + message[0].split("")[2] + message[0].split("")[3]);
			return new ResponseEntity<>(new Message(errCode, "AUTHORIZATION_ERROR", message[5]),
					HttpStatus.valueOf(errCode));
		}
	}

	@GetMapping("/report")
	public ResponseEntity<?> getAllReports(@RequestHeader(name = "Authorization") String token) {
		try {
			List<AppServiceReqReport> response = service.getAllReport(token);
			if (response.isEmpty()) {
				return new ResponseEntity<>(new Message(200, "NO DATA FOUND", null), HttpStatus.OK);
			}
			return new ResponseEntity<>(new Message(200, "DATA FOUND", response), HttpStatus.OK);
		} catch (InvalidDataAccessException e ) {
			return new ResponseEntity<>(new Message(401, e.getMessage(), null), HttpStatus.UNAUTHORIZED);
		} catch (NoRequestFoundException e) {
			return new ResponseEntity<>(new Message(404, e.getMessage(), null), HttpStatus.NOT_FOUND);
		} catch (FeignClientException e) {
			String[] message = e.getMessage().split(" ");
			int errCode = Integer.parseInt(message[0].split("")[1] + message[0].split("")[2] + message[0].split("")[3]);
			return new ResponseEntity<>(new Message(errCode, "AUTHORIZATION_ERROR", message[5]),
					HttpStatus.valueOf(errCode));
		} 
	}

	@GetMapping("/report/reportId/{reportId}")
	public ResponseEntity<?> getReportByReportId(@RequestHeader(name = "Authorization") String token,
			@PathVariable long reportId) {
		try {
			AppServiceReqReport response = service.getByReportId(token, reportId);
			return new ResponseEntity<>(new Message(200, "DATA FOUND", response), HttpStatus.OK);
		} catch (InvalidDataAccessException e) {
			return new ResponseEntity<>(new Message(401, e.getMessage(), null), HttpStatus.UNAUTHORIZED);
		} catch (NoRequestFoundException e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<>(new Message(404, e.getMessage(), null), HttpStatus.NOT_FOUND);
		} catch (FeignClientException e) {
			String[] message = e.getMessage().split(" ");
			int errCode = Integer.parseInt(message[0].split("")[1] + message[0].split("")[2] + message[0].split("")[3]);
			return new ResponseEntity<>(new Message(errCode, "AUTHORIZATION_ERROR", message[5]),
					HttpStatus.valueOf(errCode));
		}
	}
	
	@GetMapping("/report/requestId/{requestId}")
	public ResponseEntity<?> getReportByRequestId(@RequestHeader(name = "Authorization") String token,
			@PathVariable long requestId) {
		try {
			AppServiceReqReport response = service.getReportByRequestId(token, requestId);
			return new ResponseEntity<>(new Message(200, "DATA FOUND", response), HttpStatus.OK);
		} catch (InvalidDataAccessException e) {
			return new ResponseEntity<>(new Message(401, e.getMessage(), null), HttpStatus.UNAUTHORIZED);
		} catch (NoRequestFoundException e) {
			return new ResponseEntity<>(new Message(404, e.getMessage(), null), HttpStatus.NOT_FOUND);
		} catch (RequestNotExistsException e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<>(new Message(404, e.getMessage(), null), HttpStatus.NOT_FOUND);
		}  catch (UnauthorizedAccessException e) {
			return new ResponseEntity<>(new Message(401, e.getMessage(), null), HttpStatus.UNAUTHORIZED);
		} catch (FeignClientException e) {
			String[] message = e.getMessage().split(" ");
			int errCode = Integer.parseInt(message[0].split("")[1] + message[0].split("")[2] + message[0].split("")[3]);
			return new ResponseEntity<>(new Message(errCode, "AUTHORIZATION_ERROR", message[5]),
					HttpStatus.valueOf(errCode));
		} 
	}

}
