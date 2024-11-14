package com.testdemo.testdemo.service;

import java.util.List;
import java.util.Optional;

import com.testdemo.testdemo.dto.CustomerRequest;
import com.testdemo.testdemo.dto.CustomerResponse;
import com.testdemo.testdemo.exception.ResourceNotFoundException;
import com.testdemo.testdemo.mapper.CustomerMapper;
import com.testdemo.testdemo.repository.Customer;
import com.testdemo.testdemo.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {

	private final CustomerRepository customerRepository;

	private final CustomerMapper customerMapper;

	public List<CustomerResponse> getAllCustomers () {

		return customerMapper.toCustomerResponseList(customerRepository.findAll());
	}

	public CustomerResponse findCustomerById (Long customerId) {
		return Optional.ofNullable(getCustomerById(customerId))
				.map(customerMapper::toCustomerResponse)
				.orElse(null);
	}

	public CustomerResponse saveCustomer (CustomerRequest customerRequest) {
		return customerMapper.toCustomerResponse(customerRepository.save(customerMapper.toCustomer(customerRequest)));
	}

	public CustomerResponse updateCustomer (Long customerId, CustomerRequest customerRequest) {
		return Optional.ofNullable(getCustomerById(customerId))
				.map(existingCustomer -> saveCustomer(customerRequest))
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
	}

	public void deleteCustomer (Long customerId) {
		Optional.ofNullable(getCustomerById(customerId))
				.ifPresentOrElse(
						existingCustomer -> {
							customerRepository.deleteById(customerId);
						},
						() -> {
							log.error("there is not customer with id : {}", customerId);
							throw new ResourceNotFoundException("customer not found with id : " + customerId.toString());
						}
				);
	}

	private Customer getCustomerById (Long customerId) {
		return customerRepository.findById(customerId).orElse(null);
	}

}
