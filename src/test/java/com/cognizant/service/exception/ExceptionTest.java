package com.cognizant.service.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ExceptionTest {
	
	InvalidDataAccessException accessException;
	NoRequestFoundException noRequestFoundException;
	RequestNotExistsException requestNotExistsException;
	ServiceAlreadyProvidedException providedException;
	UnauthorizedAccessException unauthorizedAccessException;
	
	@Test
	void testLoadInvalidDataAccessException() {
		assertThat(accessException).isNull();
	}
	@Test
	void testLoadNoRequestFoundException() {
		assertThat(noRequestFoundException).isNull();
	}
	@Test
	void testLoadRequestNotExistsException() {
		assertThat(requestNotExistsException).isNull();
	}
	@Test
	void testLoadServiceAlreadyProvidedException() {
		assertThat(providedException).isNull();
	}
	@Test
	void testLoadUnauthorizedAccessException() {
		assertThat(unauthorizedAccessException).isNull();
	}

}
