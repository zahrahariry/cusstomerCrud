package com.testdemo.testdemo;

import java.util.List;

import com.testdemo.testdemo.dto.OrderRequest;
import com.testdemo.testdemo.dto.OrderResponse;
import com.testdemo.testdemo.repository.Customer;
import com.testdemo.testdemo.repository.CustomerRepository;
import com.testdemo.testdemo.repository.Order;
import com.testdemo.testdemo.repository.OrderRepository;
import com.testdemo.testdemo.repository.Product;
import com.testdemo.testdemo.repository.ProductRepository;
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
public class OrderControllerIT {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ProductRepository productRepository;

	@BeforeEach
	void setUp() {
		orderRepository.deleteAll();
	}

	@Test
	void saveOrder_validInput_success () {
		Customer customer = createCustomer( "first-name-1", "last-name-1", "email-1",
				"description-1");

		Product product = createProduct(2d, "name-1");

		ResponseEntity<OrderResponse> orderResponse = restTemplate.postForEntity(
				createURLWithPort("/order"),createOrderRequest(customer.getId(), product.getId(), 3), OrderResponse.class
		);

		assertThat(orderResponse).isNotNull();
		assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		OrderResponse orderResponseBody = orderResponse.getBody();
		assertThat(orderResponseBody).isNotNull();
		assertThat(orderResponseBody.getId()).isNotNull();
		assertThat(orderResponseBody.getCustomer().getId()).isNotNull();
		assertThat(orderResponseBody.getCustomer().getDescription()).isEqualTo("description-1");
		assertThat(orderResponseBody.getCustomer().getFirstName()).isEqualTo("first-name-1");
		assertThat(orderResponseBody.getCustomer().getLastName()).isEqualTo("last-name-1");
		assertThat(orderResponseBody.getCustomer().getEmail()).isEqualTo("email-1");
		assertThat(orderResponseBody.getProduct().getId()).isNotNull();
		assertThat(orderResponseBody.getProduct().getName()).isEqualTo("name-1");
		assertThat(orderResponseBody.getProduct().getPrice()).isEqualTo(2D);
		assertThat(orderResponseBody.getCount()).isEqualTo(3);
	}

	@Test
	void getAllOrders_success () {
		createOrder("first-name-1", "last-name-1", "email-1", "description-1",
				"name-1", 1d, 1);

		createOrder("first-name-2", "last-name-2", "email-2", "description-2",
				"name-2", 2d, 2);

		createOrder("first-name-3", "last-name-3", "email-3", "description-3",
				"name-3", 3d, 3);

		ResponseEntity<List<OrderResponse>> orderResponseList = restTemplate.exchange(
				createURLWithPort("/order"),
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<OrderResponse>>() {}
		);

		assertThat(orderResponseList).isNotNull();
		assertThat(orderResponseList.getStatusCode()).isEqualTo(HttpStatus.OK);
		List<OrderResponse> orderResponseBody = orderResponseList.getBody();
		assertThat(orderResponseBody).isNotNull();
		assertThat(orderResponseBody.size()).isEqualTo(3);
		OrderResponse orderResponse1 = orderResponseBody.get(0);
		assertThat(orderResponse1).isNotNull();
		assertThat(orderResponse1.getCustomer()).isNotNull();
		assertThat(orderResponse1.getCustomer().getId()).isNotNull();
		assertThat(orderResponse1.getCustomer().getFirstName()).isEqualTo("first-name-1");

		OrderResponse orderResponse2 = orderResponseBody.get(1);
		assertThat(orderResponse2).isNotNull();
		assertThat(orderResponse2.getCustomer()).isNotNull();
		assertThat(orderResponse2.getCustomer().getId()).isNotNull();
		assertThat(orderResponse2.getCustomer().getLastName()).isEqualTo("last-name-2");

		OrderResponse orderResponse3 = orderResponseBody.get(2);
		assertThat(orderResponse3).isNotNull();
		assertThat(orderResponse3.getCustomer()).isNotNull();
		assertThat(orderResponse3.getCustomer().getId()).isNotNull();
		assertThat(orderResponse3.getCustomer().getDescription()).isEqualTo("description-3");
	}

	@Test
	void getOrderById_success () {

		createOrder("first-name-1", "last-name-1", "email-1", "description-1",
				"name-1", 1d, 1);

		Long id = createOrder("first-name-2", "last-name-2", "email-2", "description-2",
				"name-2", 2d, 2);

		createOrder("first-name-3", "last-name-3", "email-3", "description-3",
				"name-3", 3d, 3);

		ResponseEntity<OrderResponse> orderResponse= restTemplate.exchange(
				createURLWithPort("/order/"+ id.toString()),
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<OrderResponse>() {}
		);

		assertThat(orderResponse).isNotNull();
		assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		OrderResponse orderResponseBody = orderResponse.getBody();
		assertThat(orderResponseBody).isNotNull();
		assertThat(orderResponseBody.getCustomer()).isNotNull();
		assertThat(orderResponseBody.getCustomer().getId()).isNotNull();
		assertThat(orderResponseBody.getProduct()).isNotNull();
		assertThat(orderResponseBody.getProduct().getId()).isNotNull();
		assertThat(orderResponseBody.getProduct().getName()).isEqualTo("name-2");
	}

	@Test
	void deleteOrder_validInput_success () {
		createOrder("first-name-1", "last-name-1", "email-1", "description-1",
				"name-1", 1d, 1);

		Long id = createOrder("first-name-2", "last-name-2", "email-2", "description-2",
				"name-2", 2d, 2);

		createOrder("first-name-3", "last-name-3", "email-3", "description-3",
				"name-3", 3d, 3);


		ResponseEntity<Void> orderResponse = restTemplate.exchange(
				createURLWithPort("/order/"+ id.toString()),
				HttpMethod.DELETE,
				null,
				Void.class
		);

		assertThat(orderResponse).isNotNull();
		assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		List<Order> remainingOrder = orderRepository.findAll();
		assertThat(remainingOrder.size()).isEqualTo(2);
		Order order1 = remainingOrder.get(0);
		assertThat(order1).isNotNull();
		assertThat(order1.getId()).isNotNull();
		assertThat(order1.getCustomer()).isNotNull();
		assertThat(order1.getProduct()).isNotNull();
		assertThat(order1.getProduct().getId()).isNotNull();
		assertThat(order1.getCustomer().getFirstName()).isEqualTo("first-name-1");
		assertThat(order1.getProduct().getPrice()).isEqualTo(1d);

		Order order2 = remainingOrder.get(1);
		assertThat(order2).isNotNull();
		assertThat(order2.getId()).isNotNull();
		assertThat(order2.getCustomer()).isNotNull();
		assertThat(order2.getProduct()).isNotNull();
		assertThat(order2.getProduct().getId()).isNotNull();
		assertThat(order2.getCustomer().getLastName()).isEqualTo("last-name-3");
		assertThat(order2.getProduct().getPrice()).isEqualTo(3d);

	}

	@Test
	void deleteOrder_inValidInput_fail () {
		createOrder("first-name-1", "last-name-1", "email-1", "description-1",
				"name-1", 1d, 1);

		createOrder("first-name-2", "last-name-2", "email-2", "description-2",
				"name-2", 2d, 2);

		createOrder("first-name-3", "last-name-3", "email-3", "description-3",
				"name-3", 3d, 3);

		ResponseEntity<Void> orderResponse = restTemplate.exchange(
				createURLWithPort("/order/95"),
				HttpMethod.DELETE,
				null,
				Void.class
		);

		assertThat(orderResponse).isNotNull();
		assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		List<Order> remainingOrder = orderRepository.findAll();
		assertThat(remainingOrder.size()).isEqualTo(3);

		Order order1 = remainingOrder.get(0);
		assertThat(order1.getCustomer()).isNotNull();
		assertThat(order1.getCustomer().getId()).isNotNull();
		assertThat(order1.getCustomer().getFirstName()).isEqualTo("first-name-1");
		assertThat(order1.getProduct()).isNotNull();
		assertThat(order1.getProduct().getId()).isNotNull();
		assertThat(order1.getProduct().getPrice()).isEqualTo(1d);

		Order order2 = remainingOrder.get(1);
		assertThat(order2.getCustomer()).isNotNull();
		assertThat(order2.getCustomer().getId()).isNotNull();
		assertThat(order2.getCustomer().getDescription()).isEqualTo("description-2");
		assertThat(order2.getProduct()).isNotNull();
		assertThat(order2.getProduct().getId()).isNotNull();
		assertThat(order2.getProduct().getName()).isEqualTo("name-2");

		Order order3 = remainingOrder.get(2);
		assertThat(order3.getCustomer()).isNotNull();
		assertThat(order3.getCustomer().getId()).isNotNull();
		assertThat(order3.getCustomer().getLastName()).isEqualTo("last-name-3");
		assertThat(order3.getProduct()).isNotNull();
		assertThat(order3.getProduct().getId()).isNotNull();
		assertThat(order3.getProduct().getPrice()).isEqualTo(3L);
	}

	@Test
	void updateOrder_validInput_success () {
		createOrder("first-name-1", "last-name-1", "email-1", "description-1",
				"name-1", 1d, 1);

		Long id = createOrder("first-name-2", "last-name-2", "email-2", "description-2",
				"name-2", 2d, 2);

		createOrder("first-name-3", "last-name-3", "email-3", "description-3",
				"name-3", 3d, 3);

		Customer updateCustomer = createCustomer("update-first-name", "update-last-name", "update-email",
				"update-description");

		Product updateProduct = createProduct(1000d, "update-name");

		OrderRequest orderRequest = createOrderRequest(updateCustomer.getId(), updateProduct.getId(), 12);

		HttpEntity<OrderRequest> requestEntity = new HttpEntity<>(orderRequest);

		ResponseEntity<OrderResponse> orderResponse = restTemplate.exchange(
				createURLWithPort("/order/"+ id.toString()),
				HttpMethod.PUT,
				requestEntity,
				OrderResponse.class
		);

		assertThat(orderResponse).isNotNull();
		assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		OrderResponse orderResponseBody = orderResponse.getBody();
		assertThat(orderResponseBody).isNotNull();
		assertThat(orderResponseBody.getId()).isNotNull();
		assertThat(orderResponseBody.getCustomer()).isNotNull();
		assertThat(orderResponseBody.getCustomer().getId()).isNotNull();
		assertThat(orderResponseBody.getCustomer().getEmail()).isEqualTo("update-email");
		assertThat(orderResponseBody.getCustomer().getFirstName()).isEqualTo("update-first-name");

		assertThat(orderResponseBody.getProduct()).isNotNull();
		assertThat(orderResponseBody.getProduct().getId()).isNotNull();
		assertThat(orderResponseBody.getProduct().getName()).isEqualTo("update-name");
		assertThat(orderResponseBody.getProduct().getPrice()).isEqualTo(1000d);
	}

	@Test
	void getOrdersByCustomerId_success () {

		Customer customer = createCustomer("first-name-1", "last-name-1", "email-1", "description-1");

		Product product = createProduct(1d, "name-1");

		createOrder(customer, product, 1);

		ResponseEntity<List<OrderResponse>> orderResponses= restTemplate.exchange(
				createURLWithPort("/order/customer/"+ customer.getId()),
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<OrderResponse>>() {}
		);

		assertThat(orderResponses).isNotNull();
		assertThat(orderResponses.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(orderResponses.getBody()).isNotNull();
		assertThat(orderResponses.getBody().size()).isEqualTo(1);
		OrderResponse orderResponse = orderResponses.getBody().get(0);
		assertThat(orderResponse).isNotNull();
		assertThat(orderResponse.getId()).isNotNull();
		assertThat(orderResponse.getCustomer()).isNotNull();
		assertThat(orderResponse.getCustomer().getId()).isNotNull();
		assertThat(orderResponse.getCustomer().getId()).isEqualTo(customer.getId());
		assertThat(orderResponse.getCustomer().getEmail()).isEqualTo("email-1");
		assertThat(orderResponse.getCustomer().getLastName()).isEqualTo("last-name-1");
	}

	@Test
	void getOrdersByProductId_success () {

		Customer customer = createCustomer("first-name-1", "last-name-1", "email-1", "description-1");

		Product product = createProduct(1d, "name-1");

		createOrder(customer, product, 1);

		ResponseEntity<List<OrderResponse>> orderResponses= restTemplate.exchange(
				createURLWithPort("/order/product/"+ product.getId()),
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<OrderResponse>>() {}
		);

		assertThat(orderResponses).isNotNull();
		assertThat(orderResponses.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(orderResponses.getBody()).isNotNull();
		assertThat(orderResponses.getBody().size()).isEqualTo(1);
		OrderResponse orderResponse = orderResponses.getBody().get(0);
		assertThat(orderResponse).isNotNull();
		assertThat(orderResponse.getId()).isNotNull();
		assertThat(orderResponse.getProduct()).isNotNull();
		assertThat(orderResponse.getProduct().getId()).isNotNull();
		assertThat(orderResponse.getProduct().getId()).isEqualTo(customer.getId());
		assertThat(orderResponse.getProduct().getName()).isEqualTo("name-1");
		assertThat(orderResponse.getProduct().getPrice()).isEqualTo(1d);
	}

	@Test
	void getOrdersByCustomerId_fail () {

		Customer customer = createCustomer("first-name-1", "last-name-1", "email-1", "description-1");

		Product product = createProduct(1d, "name-1");

		createOrder(customer, product, 1);

		ResponseEntity<List<OrderResponse>> orderResponses= restTemplate.exchange(
				createURLWithPort("/order/customer/"+ -55L),
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<OrderResponse>>() {}
		);

		assertThat(orderResponses).isNotNull();
		assertThat(orderResponses.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(orderResponses.getBody()).isNotNull();
		assertThat(orderResponses.getBody().size()).isEqualTo(0);
	}

	@Test
	void getOrdersByProductId_fail () {

		Customer customer = createCustomer("first-name-1", "last-name-1", "email-1", "description-1");

		Product product = createProduct(1d, "name-1");

		createOrder(customer, product, 1);

		ResponseEntity<List<OrderResponse>> orderResponses= restTemplate.exchange(
				createURLWithPort("/order/product/"+ -55L),
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<OrderResponse>>() {}
		);

		assertThat(orderResponses).isNotNull();
		assertThat(orderResponses.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(orderResponses.getBody()).isNotNull();
		assertThat(orderResponses.getBody().size()).isEqualTo(0);
	}

	@Test
	void updateOrder_inValidInput_fail () {
		createOrder("first-name-1", "last-name-1", "email-1", "description-1",
				"name-1", 1d, 1);

		createOrder("first-name-2", "last-name-2", "email-2", "description-2",
				"name-2", 2d, 2);

		createOrder("first-name-3", "last-name-3", "email-3", "description-3",
				"name-3", 3d, 3);

		OrderRequest orderRequest = createOrderRequest(10L, 11L, 12);

		HttpEntity<OrderRequest> requestEntity = new HttpEntity<>(orderRequest);

		ResponseEntity<OrderResponse> orderResponse = restTemplate.exchange(
				createURLWithPort("/order/44"),
				HttpMethod.PUT,
				requestEntity,
				OrderResponse.class
		);

		assertThat(orderResponse).isNotNull();
		assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void saveOrder_inValidInput_fail () {

		ResponseEntity<OrderResponse> orderResponse = restTemplate.postForEntity(
				createURLWithPort("/order"),null, OrderResponse.class
		);

		assertThat(orderResponse).isNotNull();
		assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		OrderResponse orderResponseBody = orderResponse.getBody();
		assertThat(orderResponseBody).isNotNull();
		assertThat(orderResponseBody.getId()).isNull();
	}

	private OrderRequest createOrderRequest (Long customerId, Long productId, Integer count) {
		OrderRequest orderRequest = new OrderRequest();
		orderRequest.setCustomerId(customerId);
		orderRequest.setProductId(productId);
		orderRequest.setCount(count);
		return orderRequest;
	}

	private Long createOrder (String customerFirstName, String customerLastName,
			String customerEmail, String customerDescription,String productName,
			Double productPrice, Integer count) {
		Order order = new Order();
		order.setCustomer(createCustomer(customerFirstName, customerLastName, customerEmail, customerDescription));
		order.setProduct(createProduct(productPrice, productName));
		order.setCount(count);
		Order savedOrder = orderRepository.save(order);
		return savedOrder.getId();
	}

	private Long createOrder (Customer  customer, Product product, Integer count) {
		Order order = new Order();
		order.setCustomer(customer);
		order.setProduct(product);
		order.setCount(count);
		Order savedOrder = orderRepository.save(order);
		return savedOrder.getId();
	}

	private Customer createCustomer (String firstName, String lastName, String email, String description) {
		Customer customer = new Customer();
		customer.setFirstName(firstName);
		customer.setLastName(lastName);
		customer.setEmail(email);
		customer.setDescription(description);
		customerRepository.save(customer);
		return customer;
	}

	private Product createProduct (Double price, String name) {
		Product product = new Product();
		product.setPrice(price);
		product.setName(name);
		productRepository.save(product);
		return product;
	}

	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}
}
