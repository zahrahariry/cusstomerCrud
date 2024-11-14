package com.testdemo.testdemo.service;

import java.util.List;
import java.util.Optional;

import com.testdemo.testdemo.dto.OrderRequest;
import com.testdemo.testdemo.dto.OrderResponse;
import com.testdemo.testdemo.exception.ResourceNotFoundException;
import com.testdemo.testdemo.mapper.CustomerMapper;
import com.testdemo.testdemo.mapper.OrderMapper;
import com.testdemo.testdemo.mapper.ProductMapper;
import com.testdemo.testdemo.repository.Order;
import com.testdemo.testdemo.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;

	private final CustomerService customerService;

	private final ProductService productService;

	private final OrderMapper orderMapper;

	private final CustomerMapper customerMapper;

	private final ProductMapper productMapper;

	public List<OrderResponse> getAllOrders () {
		return orderMapper.toOrderResponseList(orderRepository.findAll());
	}

	public OrderResponse findOrderById (Long orderId) {
		return Optional.ofNullable(getOrderById(orderId))
				.map(orderMapper::toOrderResponse)
				.orElse(null);
	}

	public OrderResponse saveOrder(OrderRequest orderRequest) {
		return Optional.of(new Order())
				.map(order -> {
					order.setCustomer(Optional.ofNullable(orderRequest.getCustomerId())
							.map(customerService::findCustomerById)
							.map(customerMapper::toCustomer)
							.orElseThrow(() -> new ResourceNotFoundException("Customer not found")));

					order.setProduct(Optional.ofNullable(orderRequest.getProductId())
							.map(productService::findProductById)
							.map(productMapper::toProduct)
							.orElseThrow(() -> new ResourceNotFoundException("Product not found")));

					order.setCount(orderRequest.getCount());
					return orderRepository.save(order);
				})
				.map(orderMapper::toOrderResponse)
				.orElseThrow(() -> new ResourceNotFoundException("Could not create order by customerId: "+orderRequest.getCustomerId()));
	}

	public OrderResponse updateOrder (Long orderId, OrderRequest orderRequest) {
		return Optional.ofNullable(getOrderById(orderId))
				.map(order -> {
					Optional.ofNullable(orderRequest.getCustomerId())
							.map(customerService::findCustomerById)
							.map(customerMapper::toCustomer)
							.ifPresent(order::setCustomer);

					Optional.ofNullable(orderRequest.getProductId())
							.map(productService::findProductById)
							.map(productMapper::toProduct)
							.ifPresent(order::setProduct);

					return orderRepository.save(order);
				})
				.map(orderMapper::toOrderResponse)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
	}

	public void deleteOrder (Long orderId) {
		Optional.ofNullable(getOrderById(orderId))
				.ifPresentOrElse(
						existingOrder -> {
							orderRepository.deleteById(orderId);
						},
						() -> {
							log.error("there is not order with id : {}", orderId);
							throw new ResourceNotFoundException("order not found with id : " + orderId.toString());
						}
				);
	}

	private Order getOrderById (Long orderId) {
		return orderRepository.findById(orderId).orElse(null);
	}
}
