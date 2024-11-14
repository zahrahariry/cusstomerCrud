package com.testdemo.testdemo;

import java.util.List;

import com.testdemo.testdemo.dto.CustomerRequest;
import com.testdemo.testdemo.dto.CustomerResponse;
import com.testdemo.testdemo.mapper.CustomerMapper;
import com.testdemo.testdemo.repository.Customer;
import com.testdemo.testdemo.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerControllerIT {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CustomerMapper customerMapper;

	@BeforeEach
	void setUp() {
		customerRepository.deleteAll();
	}

	@Test
	void saveCustomer_validInput_success () {

		ResponseEntity<CustomerResponse> customerResponse = restTemplate.postForEntity(
				createURLWithPort("/customer"),createCustomerRequest("test-first-name", "test-last-name",
						"test-email@test.com", "test-description"), CustomerResponse.class
		);

		assertThat(customerResponse).isNotNull();
		assertThat(customerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		CustomerResponse customerResponseBody = customerResponse.getBody();
		assertThat(customerResponseBody).isNotNull();
		assertThat(customerResponseBody.getId()).isNotNull();
		assertThat(customerResponseBody.getFirstName()).isEqualTo("test-first-name");
		assertThat(customerResponseBody.getLastName()).isEqualTo("test-last-name");
		assertThat(customerResponseBody.getEmail()).isEqualTo("test-email@test.com");
		assertThat(customerResponseBody.getDescription()).isEqualTo("test-description");
	}

	@Test
	void getAllCustomers_success () {
		createCustomer("first-name-1", "last-name-1", "email-1", "description-1");
		createCustomer("first-name-2", "last-name-2", "email-2", "description-2");
		createCustomer("first-name-3", "last-name-3", "email-3", "description-3");

		ResponseEntity<List<CustomerResponse>> customerResponseList = restTemplate.exchange(
				createURLWithPort("/customer"),
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<CustomerResponse>>() {}
		);

		assertThat(customerResponseList).isNotNull();
		assertThat(customerResponseList.getStatusCode()).isEqualTo(HttpStatus.OK);
		List<CustomerResponse> customerResponseBody = customerResponseList.getBody();
		assertThat(customerResponseBody).isNotNull();
		assertThat(customerResponseBody.size()).isEqualTo(3);
		assertThat(customerResponseBody.get(0).getFirstName()).isEqualTo("first-name-1");
		assertThat(customerResponseBody.get(0).getId()).isNotNull();
		assertThat(customerResponseBody.get(1).getFirstName()).isEqualTo("first-name-2");
		assertThat(customerResponseBody.get(1).getId()).isNotNull();
		assertThat(customerResponseBody.get(2).getFirstName()).isEqualTo("first-name-3");
		assertThat(customerResponseBody.get(2).getId()).isNotNull();
	}

	@Test
	void getCustomerById_success () {
		createCustomer("first-name-1", "last-name-1", "email-1", "description-1");
		Long id = createCustomer("first-name-2", "last-name-2", "email-2", "description-2");
		createCustomer("first-name-3", "last-name-3", "email-3", "description-3");

		ResponseEntity<CustomerResponse> customerResponse = restTemplate.exchange(
				createURLWithPort("/customer/"+ id.toString()),
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<CustomerResponse>() {}
		);

		assertThat(customerResponse).isNotNull();
		assertThat(customerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		CustomerResponse customerResponseBody = customerResponse.getBody();
		assertThat(customerResponseBody).isNotNull();
		assertThat(customerResponseBody.getFirstName()).isEqualTo("first-name-2");
		assertThat(customerResponseBody.getId()).isNotNull();
		assertThat(customerResponseBody.getId()).isEqualTo(2L);
		assertThat(customerResponseBody.getLastName()).isEqualTo("last-name-2");
		assertThat(customerResponseBody.getEmail()).isEqualTo("email-2");
		assertThat(customerResponseBody.getDescription()).isEqualTo("description-2");
	}

	@Test
	void deleteCustomer_validInput_success () {
		createCustomer( "first-name-1", "last-name-1", "email-1", "description-1");
		Long id = createCustomer( "first-name-2", "last-name-2", "email-2", "description-2");
		createCustomer( "first-name-3", "last-name-3", "email-3", "description-3");


		ResponseEntity<Void> customerResponse = restTemplate.exchange(
				createURLWithPort("/customer/"+ id.toString()),
				HttpMethod.DELETE,
				null,
				Void.class
		);

		assertThat(customerResponse).isNotNull();
		assertThat(customerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		List<Customer> remainingCustomer = customerRepository.findAll();
		assertThat(remainingCustomer.size()).isEqualTo(2);
		assertThat(remainingCustomer.get(0).getFirstName()).isEqualTo("first-name-1");
		assertThat(remainingCustomer.get(1).getFirstName()).isEqualTo("first-name-3");
	}

	@Test
	void deleteCustomer_inValidInput_fail () {
		createCustomer("first-name-1", "last-name-1", "email-1", "description-1");
		createCustomer("first-name-2", "last-name-2", "email-2", "description-2");
		createCustomer("first-name-3", "last-name-3", "email-3", "description-3");

		ResponseEntity<Void> customerResponse = restTemplate.exchange(
				createURLWithPort("/customer/95"),
				HttpMethod.DELETE,
				null,
				Void.class
		);

		assertThat(customerResponse).isNotNull();
		assertThat(customerResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		List<Customer> remainingCustomer = customerRepository.findAll();
		assertThat(remainingCustomer.size()).isEqualTo(3);
		assertThat(remainingCustomer.get(0).getFirstName()).isEqualTo("first-name-1");
		assertThat(remainingCustomer.get(1).getFirstName()).isEqualTo("first-name-2");
		assertThat(remainingCustomer.get(2).getFirstName()).isEqualTo("first-name-3");
	}

	@Test
	void updateCustomer_validInput_success () {
		createCustomer( "first-name-1", "last-name-1", "email-1", "description-1");
		Long id = createCustomer( "first-name-2", "last-name-2", "email-2", "description-2");
		createCustomer( "first-name-3", "last-name-3", "email-3", "description-3");

		CustomerRequest customerRequest = createCustomerRequest("first-name-6", "last-name-2",
				"email-6", "description-66");

		HttpEntity<CustomerRequest> requestEntity = new HttpEntity<>(customerRequest);

		ResponseEntity<CustomerResponse> customerResponse = restTemplate.exchange(
				createURLWithPort("/customer/"+ id.toString()),
				HttpMethod.PUT,
				requestEntity,
				CustomerResponse.class
		);

		assertThat(customerResponse).isNotNull();
		assertThat(customerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		CustomerResponse customerResponseBody = customerResponse.getBody();
		assertThat(customerResponseBody).isNotNull();
		assertThat(customerResponseBody.getFirstName()).isEqualTo("first-name-6");
		assertThat(customerResponseBody.getId()).isNotNull();
		assertThat(customerResponseBody.getLastName()).isEqualTo("last-name-2");
		assertThat(customerResponseBody.getEmail()).isEqualTo("email-6");
		assertThat(customerResponseBody.getDescription()).isEqualTo("description-66");
	}

	@Test
	void updateCustomer_inValidInput_fail () {
		createCustomer( "first-name-1", "last-name-1", "email-1", "description-1");
		createCustomer( "first-name-2", "last-name-2", "email-2", "description-2");
		createCustomer( "first-name-3", "last-name-3", "email-3", "description-3");

		CustomerRequest customerRequest = createCustomerRequest("first-name-6", "last-name-2",
				"email-6", "description-66");

		HttpEntity<CustomerRequest> requestEntity = new HttpEntity<>(customerRequest);

		ResponseEntity<CustomerResponse> customerResponse = restTemplate.exchange(
				createURLWithPort("/customer/44"),
				HttpMethod.PUT,
				requestEntity,
				CustomerResponse.class
		);

		assertThat(customerResponse).isNotNull();
		assertThat(customerResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void saveCustomer_inValidInput_fail () {

		ResponseEntity<CustomerResponse> customerResponse = restTemplate.postForEntity(
				createURLWithPort("/customer"),null, CustomerResponse.class
		);

		assertThat(customerResponse).isNotNull();
		assertThat(customerResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		CustomerResponse customerResponseBody = customerResponse.getBody();
		assertThat(customerResponseBody).isNotNull();
		assertThat(customerResponseBody.getId()).isNull();
		assertThat(customerResponseBody.getFirstName()).isNull();
		assertThat(customerResponseBody.getLastName()).isNull();
		assertThat(customerResponseBody.getEmail()).isNull();
		assertThat(customerResponseBody.getDescription()).isNull();
	}

	private CustomerRequest createCustomerRequest (String firstName, String lastName, String email, String description) {
		CustomerRequest customerRequest = new CustomerRequest();
		customerRequest.setFirstName(firstName);
		customerRequest.setLastName(lastName);
		customerRequest.setEmail(email);
		customerRequest.setDescription(description);
		return customerRequest;
	}

	private Long createCustomer (String firstName, String lastName, String email, String description) {
		Customer customer = new Customer();
		customer.setFirstName(firstName);
		customer.setLastName(lastName);
		customer.setEmail(email);
		customer.setDescription(description);
		Customer savedCustomer = customerRepository.save(customer);
		return savedCustomer.getId();
	}

	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}
}
