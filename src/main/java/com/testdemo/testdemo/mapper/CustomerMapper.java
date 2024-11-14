package com.testdemo.testdemo.mapper;

import java.util.List;

import com.testdemo.testdemo.dto.CustomerRequest;
import com.testdemo.testdemo.dto.CustomerResponse;
import com.testdemo.testdemo.repository.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

	CustomerResponse toCustomerResponse (Customer customer);

	List<CustomerResponse> toCustomerResponseList (List<Customer> customers);

	Customer toCustomer (CustomerRequest customerRequest);

	Customer toCustomer(CustomerResponse customerResponse);
}
