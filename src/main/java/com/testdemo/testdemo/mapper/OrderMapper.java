package com.testdemo.testdemo.mapper;

import java.util.List;

import com.testdemo.testdemo.dto.CustomerResponse;
import com.testdemo.testdemo.dto.OrderResponse;
import com.testdemo.testdemo.dto.ProductResponse;
import com.testdemo.testdemo.repository.Customer;
import com.testdemo.testdemo.repository.Order;
import com.testdemo.testdemo.repository.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface OrderMapper {

	@Mapping(target = "customer", source = "order.customer", qualifiedByName = "toCustomerResponse")
	@Mapping(target = "product", source = "order.product", qualifiedByName = "toProductResponse")
	OrderResponse toOrderResponse (Order order);

	List<OrderResponse> toOrderResponseList (List<Order> orders);

	@Named("toCustomerResponse")
	default CustomerResponse toCustomerResponse (Customer customer) {
		CustomerResponse customerResponse = new CustomerResponse();
		customerResponse.setId(customer.getId());
		customerResponse.setEmail(customer.getEmail());
		customerResponse.setDescription(customer.getDescription());
		customerResponse.setFirstName(customer.getFirstName());
		customerResponse.setLastName(customer.getLastName());
		return customerResponse;
	}

	@Named("toProductResponse")
	default ProductResponse toProductResponse (Product product) {
		ProductResponse productResponse = new ProductResponse();
		productResponse.setId(product.getId());
		productResponse.setName(product.getName());
		productResponse.setPrice(product.getPrice());
		return productResponse;
	}
}
