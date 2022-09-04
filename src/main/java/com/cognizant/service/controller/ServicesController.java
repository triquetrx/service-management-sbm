package com.cognizant.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class ServicesController {

	@Autowired
	RequestService service;

	@PostMapping
	public ResponseEntity<?> createServiceBooking(@RequestHeader(name = "Authorization") String token,
			@RequestBody ServiceRequestDTO request) {
		try {
			ServiceResponse response = service.newServiceRequest(token, request);
			return new ResponseEntity<>(response, HttpStatus.OK);
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
			return new ResponseEntity<>(new Message(200, "DATA_FOUND", myProductRequests), HttpStatus.OK);
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
				return new ResponseEntity<>(new Message(200, "NO REQUESTS RAISED", myRequests), HttpStatus.OK);
			}
			return new ResponseEntity<>(new Message(200, "DATA_FOUND", myRequests), HttpStatus.OK);
		} catch (InvalidDataAccessException e) {
			// TODO Auto-generated catch block
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
			return new ResponseEntity<>(new Message(200, "ITEM WITH ID " + id + " DELETED", response), HttpStatus.OK);
		} catch (InvalidDataAccessException e) {
			return new ResponseEntity<>(new Message(401, e.getMessage(), null), HttpStatus.UNAUTHORIZED);
		} catch (RequestNotExistsException e) {
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
			return new ResponseEntity<>(new Message(200, "ITEM WITH ID " + id + " UPDATED", response), HttpStatus.OK);
		} catch (InvalidDataAccessException e) {
			return new ResponseEntity<>(new Message(401, e.getMessage(), null), HttpStatus.UNAUTHORIZED);
		} catch (RequestNotExistsException e) {
			return new ResponseEntity<>(new Message(401, e.getMessage(), null), HttpStatus.NOT_FOUND);
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
				return new ResponseEntity<>(new Message(200, "NO REQUEST FOUND FOR USER ID " + userId, response),
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
			return new ResponseEntity<>(new Message(200, "DATA SAVED", reqReport), HttpStatus.OK);
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

	@GetMapping("/report/{userId}")
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
		} catch (InvalidDataAccessException e) {
			return new ResponseEntity<>(new Message(401, e.getMessage(), null), HttpStatus.UNAUTHORIZED);
		} catch (FeignClientException e) {
			String[] message = e.getMessage().split(" ");
			int errCode = Integer.parseInt(message[0].split("")[1] + message[0].split("")[2] + message[0].split("")[3]);
			return new ResponseEntity<>(new Message(errCode, "AUTHORIZATION_ERROR", message[5]),
					HttpStatus.valueOf(errCode));
		}
	}

	@GetMapping("/report/{reportId}")
	public ResponseEntity<?> getReportByReportId(@RequestHeader(name = "Authorization") String token,
			@PathVariable long reportId) {
		try {
			AppServiceReqReport response = service.getByReportId(token, reportId);
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

}
