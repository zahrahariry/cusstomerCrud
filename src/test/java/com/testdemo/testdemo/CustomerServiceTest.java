package com.testdemo.testdemo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.testdemo.testdemo.dto.CustomerRequest;
import com.testdemo.testdemo.dto.CustomerResponse;
import com.testdemo.testdemo.exception.ResourceNotFoundException;
import com.testdemo.testdemo.mapper.CustomerMapper;
import com.testdemo.testdemo.repository.Customer;
import com.testdemo.testdemo.repository.CustomerRepository;
import com.testdemo.testdemo.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class})
public class CustomerServiceTest {

	@Mock
	private CustomerRepository customerRepository;

	@Spy
	private CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);

	@InjectMocks
	private CustomerService customerService;

	@Test
	void getAllCustomers_success () {
		Customer firstCustomer = createCustomer(1L, "first-name-1", "last-name-1",
				"description-1", "email-1");

		Customer secondCustomer = createCustomer(2L, "first-name-2", "last-name-2",
				"description-2", "email-2");

		Customer thirdCustomer = createCustomer(3L, "first-name-3", "last-name-3",
				"description-3", "email-3");

		when(customerRepository.findAll()).thenReturn(new ArrayList<>(List.of(firstCustomer, secondCustomer, thirdCustomer)));

		List<CustomerResponse> customerResponses = customerService.getAllCustomers();

		assertThat(customerResponses).isNotNull();
		assertThat(customerResponses.size()).isEqualTo(3);
		CustomerResponse customerResponse = customerResponses.get(1);
		assertThat(customerResponse.getId()).isNotNull();
		assertThat(customerResponse.getId()).isEqualTo(2L);
		assertThat(customerResponse.getFirstName()).isEqualTo("first-name-2");
		assertThat(customerResponse.getLastName()).isEqualTo("last-name-2");
		assertThat(customerResponse.getEmail()).isEqualTo("email-2");
		assertThat(customerResponse.getDescription()).isEqualTo("description-2");
	}

	@Test
	void findCustomerById_validInput_success () {
		Customer customer = createCustomer(1L, "first-name-1", "last-name-1",
				"description-1", "email-1");

		when(customerRepository.findById(any())).thenReturn(Optional.of(customer));

		CustomerResponse customerResponse = customerService.findCustomerById(1L);

		assertThat(customerResponse).isNotNull();
		assertThat(customerResponse.getId()).isNotNull();
		assertThat(customerResponse.getId()).isEqualTo(1L);
		assertThat(customerResponse.getFirstName()).isEqualTo("first-name-1");
		assertThat(customerResponse.getLastName()).isEqualTo("last-name-1");
		assertThat(customerResponse.getEmail()).isEqualTo("email-1");
		assertThat(customerResponse.getDescription()).isEqualTo("description-1");
	}

	@Test
	void findCustomerById_inValidInput_fail () {
		when(customerRepository.findById(any())).thenReturn(Optional.empty());

		CustomerResponse customerResponse = customerService.findCustomerById(1L);

		assertThat(customerResponse).isNull();
	}

	@Test
	void updateCustomer_validInput_success () {
		Customer customer = createCustomer(1L, "first-name-1", "last-name-1",
				"description-1", "email-1");

		when(customerRepository.findById(any())).thenReturn(Optional.of(customer));

		customer.setFirstName("first-name-updated");
		customer.setLastName("last-name-updated");
		customer.setDescription("description-updated");

		when(customerRepository.save(any())).thenReturn(customer);

		CustomerRequest customerRequest = createCustomerRequest("first-name-updated", "last-name-updated",
				"email-1", "description-updated");

		CustomerResponse customerResponse = customerService.updateCustomer(1L, customerRequest);

		assertThat(customerResponse).isNotNull();
		assertThat(customerResponse.getId()).isNotNull();
		assertThat(customerResponse.getId()).isEqualTo(1L);
		assertThat(customerResponse.getFirstName()).isEqualTo("first-name-updated");
		assertThat(customerResponse.getLastName()).isEqualTo("last-name-updated");
		assertThat(customerResponse.getEmail()).isEqualTo("email-1");
		assertThat(customerResponse.getDescription()).isEqualTo("description-updated");
	}

	@Test
	void updateCustomer_inValidInput_fail () {

		when(customerRepository.findById(any())).thenReturn(Optional.empty());

		CustomerRequest customerRequest = createCustomerRequest("first-name-updated", "last-name-updated",
				"email-1", "description-updated");

		assertThatThrownBy(() -> customerService.updateCustomer(1L, customerRequest))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void deleteCustomer_validInput_success () {
		Customer customer = createCustomer(1L, "first-name-1", "last-name-1",
				"description-1", "email-1");

		when(customerRepository.findById(any())).thenReturn(Optional.of(customer));
		willDoNothing().given(customerRepository).deleteById(any());

		customerService.deleteCustomer(1L);

		verify(customerRepository).deleteById(1L);

	}

	@Test
	void deleteCustomer_inValidInput_fail () {

		when(customerRepository.findById(any())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> customerService.deleteCustomer(1L))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void saveCustomer_validInputs_success() {
		when(customerRepository.save(any(Customer.class))).thenReturn(createCustomer(1L, "first-name-1", "last-name-1",
				"description-1", "email-1"));

		CustomerResponse customerResponse = customerService.saveCustomer(createCustomerRequest("first-name-1", "last-name-1",
				"email-1", "description-1"));

		assertThat(customerResponse).isNotNull();
		assertThat(customerResponse.getId()).isNotNull();
		assertThat(customerResponse.getId()).isEqualTo(1L);
		assertThat(customerResponse.getFirstName()).isEqualTo("first-name-1");
		assertThat(customerResponse.getLastName()).isEqualTo("last-name-1");
		assertThat(customerResponse.getEmail()).isEqualTo("email-1");
		assertThat(customerResponse.getDescription()).isEqualTo("description-1");
	}



	private Customer createCustomer (Long id, String firstName, String lastName, String description, String email) {
		Customer customer = new Customer();
		customer.setId(id);
		customer.setEmail(email);
		customer.setDescription(description);
		customer.setLastName(lastName);
		customer.setFirstName(firstName);
		return customer;
	}

	private CustomerRequest createCustomerRequest (String firstName, String lastName, String email, String description) {
		CustomerRequest customerRequest = new CustomerRequest();
		customerRequest.setFirstName(firstName);
		customerRequest.setLastName(lastName);
		customerRequest.setEmail(email);
		customerRequest.setDescription(description);
		return customerRequest;
	}
}
