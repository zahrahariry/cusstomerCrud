package com.testdemo.testdemo.controller;

import java.util.List;

import com.testdemo.testdemo.dto.OrderRequest;
import com.testdemo.testdemo.dto.OrderResponse;
import com.testdemo.testdemo.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Slf4j
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Tag(name = "Order Controller", description = "Order management APIs")
public class OrderController {

	private final OrderService orderService;

	@GetMapping
	@Operation(summary = "get all orders")
	public ResponseEntity<List<OrderResponse>> getAllOrders () {
		log.info("going to get all orders");
		return ResponseEntity.ok(orderService.getAllOrders());
	}

	@GetMapping(path = "/{orderId}")
	@Operation(summary = "get order by orderId")
	public ResponseEntity<OrderResponse> getOrderById (@PathVariable(name = "orderId") Long orderId) {
		log.info("going to get all orders by id : {}", orderId);
		return ResponseEntity.ok(orderService.findOrderById(orderId));
	}

	@PostMapping
	@Operation(summary = "save order")
	public ResponseEntity<OrderResponse> saveOrder (@RequestBody @Valid OrderRequest orderRequest) {
		log.info("going to save order by orderRequest: {}", orderRequest);
		return ResponseEntity.ok(orderService.saveOrder(orderRequest));
	}

	@PutMapping(path = "/{orderId}")
	@Operation(summary = "update order")
	public ResponseEntity<OrderResponse> updateOrder (@PathVariable(name = "orderId") Long orderId, @RequestBody @Valid OrderRequest orderRequest) {
		log.info("going to update order by orderId : {} and orderRequest : {}", orderId, orderRequest);
		return ResponseEntity.ok(orderService.updateOrder(orderId, orderRequest));
	}

	@DeleteMapping(path = "/{orderId}")
	@Operation(summary = "delete order")
	public ResponseEntity<Void> deleteOrder (@PathVariable(name = "orderId") Long orderId) {
		log.info("going to delete order by id : {}", orderId);
		orderService.deleteOrder(orderId);
		return ResponseEntity.ok().build();
	}

	@GetMapping(path = "/customer/{customerId}")
	@Operation(summary = "get orders by customerId")
	public ResponseEntity<List<OrderResponse>> getOrdersByCustomerId (@PathVariable(name = "customerId") Long customerId) {
		log.info("going to get orders by customerId : {}", customerId);
		return ResponseEntity.ok(orderService.getCustomerOrders(customerId));
	}

	@GetMapping(path = "/product/{productId}")
	@Operation(summary = "get orders by productId")
	public ResponseEntity<List<OrderResponse>> getOrdersByProductId (@PathVariable(name = "productId") Long productId) {
		log.info("going to get orders by productId : {}", productId);
		return ResponseEntity.ok(orderService.getProductOrders(productId));
	}
}
