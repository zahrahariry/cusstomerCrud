package com.testdemo.testdemo.controller;

import java.util.List;

import com.testdemo.testdemo.dto.CustomerRequest;
import com.testdemo.testdemo.dto.CustomerResponse;
import com.testdemo.testdemo.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;


@Slf4j
@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@Tag(name = "Customer Controller", description = "Customer management APIs")
public class CustomerController {

	private final CustomerService customerService;


	@GetMapping
	@Operation(summary = "get all customers")
	public ResponseEntity<List<CustomerResponse>> getAllCustomers () {
		log.info("going to get all customers");
		return ResponseEntity.ok(customerService.getAllCustomers());
	}

	@GetMapping(path = "/{customerId}")
	@Operation(summary = "get customer by customerId")
	public ResponseEntity<CustomerResponse> getCustomerById (@PathVariable(name = "customerId") Long customerId) {
		log.info("going to get customer by id : {}", customerId);
		return ResponseEntity.ok(customerService.findCustomerById(customerId));
	}

	@PostMapping
	@Operation(summary = "save customer")
	public ResponseEntity<CustomerResponse> saveCustomer (@RequestBody @Valid CustomerRequest customerRequest) {
		log.info("going to save customer by customer request :{} ", customerRequest );
		return ResponseEntity.ok(customerService.saveCustomer(customerRequest));
	}

	@PutMapping(path = "/{customerId}")
	@Operation(summary = "update customer")
	public ResponseEntity<CustomerResponse> updateCustomer (@PathVariable(name = "customerId") Long customerId, @RequestBody @Valid CustomerRequest customerRequest) {
		log.info("going to update customer by customerId : {} and customer request : {}", customerId, customerRequest);
		return ResponseEntity.ok(customerService.updateCustomer(customerId, customerRequest));
	}

	@DeleteMapping(path = "/{customerId}")
	@Operation(summary = "delete customer")
	public ResponseEntity<Void> deleteCustomer (@PathVariable(name = "customerId") Long customerId) {
		log.info("going to delete customer by id : {}", customerId);
		customerService.deleteCustomer(customerId);
		return ResponseEntity.ok().build();
	}

}
