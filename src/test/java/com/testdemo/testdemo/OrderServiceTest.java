package com.testdemo.testdemo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.testdemo.testdemo.dto.CustomerResponse;
import com.testdemo.testdemo.dto.OrderRequest;
import com.testdemo.testdemo.dto.OrderResponse;
import com.testdemo.testdemo.dto.ProductResponse;
import com.testdemo.testdemo.exception.ResourceNotFoundException;
import com.testdemo.testdemo.mapper.CustomerMapper;
import com.testdemo.testdemo.mapper.OrderMapper;
import com.testdemo.testdemo.mapper.ProductMapper;
import com.testdemo.testdemo.repository.Customer;
import com.testdemo.testdemo.repository.CustomerRepository;
import com.testdemo.testdemo.repository.Order;
import com.testdemo.testdemo.repository.OrderRepository;
import com.testdemo.testdemo.repository.Product;
import com.testdemo.testdemo.repository.ProductRepository;
import com.testdemo.testdemo.service.CustomerService;
import com.testdemo.testdemo.service.OrderService;
import com.testdemo.testdemo.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({ MockitoExtension.class})
public class OrderServiceTest {

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private ProductRepository productRepository;

	@Spy
	private OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

	@Spy
	private CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);

	@Spy
	private ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

	@InjectMocks
	private OrderService orderService;

	@Mock
	private CustomerService customerService;

	@Mock
	private ProductService productService;

	@Test
	void getAllOrders_success () {
		Order firstOrder = createOrder(1L, 1L, "first-name-1", "last-name-1", "email-1", "description-1",
				1L, "name-1", 1d, 1);

		Order secondOrder = createOrder(2L,2L, "first-name-2", "last-name-2", "email-2", "description-2",
				2L, "name-2", 2d, 2);

		Order thirdOrder = createOrder(3L, 3L, "first-name-3", "last-name-3", "email-3", "description-3",
				3L,"name-3", 3d, 3);

		when(orderRepository.findAll()).thenReturn(new ArrayList<>(List.of(firstOrder, secondOrder, thirdOrder)));

		List<OrderResponse> orderResponses = orderService.getAllOrders();

		assertThat(orderResponses).isNotNull();
		assertThat(orderResponses.size()).isEqualTo(3);
		OrderResponse orderResponse = orderResponses.get(1);
		assertThat(orderResponse.getId()).isNotNull();
		assertThat(orderResponse.getId()).isEqualTo(2L);
		assertThat(orderResponse.getCustomer().getId()).isEqualTo(2L);
		assertThat(orderResponse.getProduct().getId()).isEqualTo(2L);
		assertThat(orderResponse.getCount()).isEqualTo(2);
	}

	@Test
	void findOrderById_validInput_success () {
		Order order =createOrder(1L, 1L, "first-name-1", "last-name-1", "email-1", "description-1",
				1L, "name-1", 1d, 1);

		when(orderRepository.findById(any())).thenReturn(Optional.of(order));

		OrderResponse orderResponse = orderService.findOrderById(1L);

		assertThat(orderResponse).isNotNull();
		assertThat(orderResponse.getId()).isNotNull();
		assertThat(orderResponse.getId()).isEqualTo(1L);
		assertThat(orderResponse.getCustomer().getId()).isEqualTo(1L);
		assertThat(orderResponse.getProduct().getId()).isEqualTo(1L);
		assertThat(orderResponse.getCount()).isEqualTo(1);
	}

	@Test
	void findOrdersByCustomerId_validInput_success () {
		Customer customer = createCustomer(1L, "first-name-1", "last-name-1", "email-1", "description-1");
		Product product = createProduct(1L, 1d, "name-1");
		Order order =createOrder(1L, customer, product, 1);

		when(orderRepository.findByCustomerId(any())).thenReturn(List.of(order));

		List<OrderResponse> orderResponse = orderService.getCustomerOrders(customer.getId());

		assertThat(orderResponse).isNotNull();
		assertThat(orderResponse.size()).isEqualTo(1);
		assertThat(orderResponse.get(0)).isNotNull();
		assertThat(orderResponse.get(0).getCustomer()).isNotNull();
		assertThat(orderResponse.get(0).getCustomer().getFirstName()).isEqualTo("first-name-1");
		assertThat(orderResponse.get(0).getCustomer().getDescription()).isEqualTo("description-1");
	}

	@Test
	void findOrdersByProductId_validInput_success () {
		Customer customer = createCustomer(1L, "first-name-1", "last-name-1", "email-1", "description-1");
		Product product = createProduct(1L, 1d, "name-1");
		Order order =createOrder(1L, customer, product, 1);

		when(orderRepository.findByProductId(any())).thenReturn(List.of(order));

		List<OrderResponse> orderResponse = orderService.getProductOrders(product.getId());

		assertThat(orderResponse).isNotNull();
		assertThat(orderResponse.size()).isEqualTo(1);
		assertThat(orderResponse.get(0)).isNotNull();
		assertThat(orderResponse.get(0).getProduct()).isNotNull();
		assertThat(orderResponse.get(0).getProduct().getName()).isEqualTo("name-1");
		assertThat(orderResponse.get(0).getProduct().getPrice()).isEqualTo(1d);
	}

	@Test
	void findOrdersByCustomerId_invalidInput_fail () {
		Customer customer = createCustomer(1L, "first-name-1", "last-name-1", "email-1", "description-1");
		Product product = createProduct(1L, 1d, "name-1");
		Order order =createOrder(1L, customer, product, 1);

		when(orderRepository.findByCustomerId(any())).thenReturn(new ArrayList<>());

		List<OrderResponse> orderResponse = orderService.getCustomerOrders(-55L);

		assertThat(orderResponse).isNotNull();
		assertThat(orderResponse.size()).isEqualTo(0);
	}

	@Test
	void findOrdersByProductId_invalidInput_fail () {
		Customer customer = createCustomer(1L, "first-name-1", "last-name-1", "email-1", "description-1");
		Product product = createProduct(1L, 1d, "name-1");
		Order order =createOrder(1L, customer, product, 1);

		when(orderRepository.findByProductId(any())).thenReturn(new ArrayList<>());

		List<OrderResponse> orderResponse = orderService.getProductOrders(-55L);

		assertThat(orderResponse).isNotNull();
		assertThat(orderResponse.size()).isEqualTo(0);
	}

	@Test
	void findOrderById_inValidInput_fail () {
		when(orderRepository.findById(any())).thenReturn(Optional.empty());

		OrderResponse orderResponse = orderService.findOrderById(1L);

		assertThat(orderResponse).isNull();
	}

	@Test
	void updateOrder_validInput_success () {
		Order order = createOrder(1L, 1L, "first-name-1", "last-name-1", "email-1", "description-1",
				1L, "name-1", 1d, 1);

		Customer updateCustomer = createCustomer(1L, "update-first-name", "update-last-name", "update-email",
				"update-description");

		Product updateProduct = createProduct(1L, 1000d, "update-name");

		OrderRequest orderRequest = createOrderRequest(updateCustomer.getId(), updateProduct.getId(), 12);

		when(orderRepository.findById(any())).thenReturn(Optional.of(order));
		when(orderRepository.save(any(Order.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));
		when(customerService.findCustomerById(any())).thenReturn(createCustomerResponse(11L, "update-first-name",
				"update-last-name", "update-email", "update-description"));
		when(productService.findProductById(any())).thenReturn(createProductResponse(111L, "update-name", 1000d));

		OrderResponse orderResponse = orderService.updateOrder(1L, orderRequest);

		assertThat(orderResponse).isNotNull();
		assertThat(orderResponse.getId()).isNotNull();
		assertThat(orderResponse.getId()).isEqualTo(1L);
		assertThat(orderResponse.getCustomer()).isNotNull();
		assertThat(orderResponse.getCustomer().getId()).isNotNull();
		assertThat(orderResponse.getCustomer().getDescription()).isEqualTo("update-description");
		assertThat(orderResponse.getCustomer().getLastName()).isEqualTo("update-last-name");
		assertThat(orderResponse.getProduct()).isNotNull();
		assertThat(orderResponse.getProduct().getId()).isNotNull();
		assertThat(orderResponse.getProduct().getName()).isEqualTo("update-name");
		assertThat(orderResponse.getCount()).isEqualTo(1);
	}

	@Test
	void updateOrder_inValidInput_fail () {

		when(orderRepository.findById(any())).thenReturn(Optional.empty());

		OrderRequest orderRequest = createOrderRequest(11L, 111L, 1);

		assertThatThrownBy(() -> orderService.updateOrder(1L, orderRequest))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void deleteOrder_validInput_success () {
		Order order = createOrder(1L, 1L, "first-name-1", "last-name-1", "email-1", "description-1",
				1L, "name-1", 1d, 1);

		when(orderRepository.findById(any())).thenReturn(Optional.of(order));
		willDoNothing().given(orderRepository).deleteById(any());

		orderService.deleteOrder(1L);

		verify(orderRepository).deleteById(1L);

	}

	@Test
	void deleteOrder_inValidInput_fail () {

		when(orderRepository.findById(any())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> orderService.deleteOrder(1L))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void saveOrder_validInputs_success() {
		when(orderRepository.save(any(Order.class)))
				.thenReturn(createOrder(1L, 1L, "first-name-1", "last-name-1", "email-1", "description-1",
						1L, "name-1", 1d, 1));
		when(customerService.findCustomerById(any()))
				.thenReturn(createCustomerResponse(1L, "first-name-1", "last-name-1",
						"email-1", "description-1"));

		when(productService.findProductById(any()))
				.thenReturn(createProductResponse(1L, "name-1", 1d));

		OrderResponse orderResponse = orderService.saveOrder(createOrderRequest(11L, 111L, 1));

		assertThat(orderResponse).isNotNull();
		assertThat(orderResponse.getId()).isNotNull();
		assertThat(orderResponse.getId()).isEqualTo(1L);
		assertThat(orderResponse.getCustomer()).isNotNull();
		assertThat(orderResponse.getCustomer().getId()).isNotNull();
		assertThat(orderResponse.getCustomer().getLastName()).isEqualTo("last-name-1");
		assertThat(orderResponse.getCustomer().getDescription()).isEqualTo("description-1");
		assertThat(orderResponse.getProduct()).isNotNull();
		assertThat(orderResponse.getProduct().getId()).isNotNull();
		assertThat(orderResponse.getProduct().getName()).isEqualTo("name-1");
		assertThat(orderResponse.getProduct().getPrice()).isEqualTo(1d);
		assertThat(orderResponse.getCount()).isEqualTo(1);
	}



	private Order createOrder (Long orderId, Long customerId, String customerFirstName, String customerLastName,
			String customerEmail, String customerDescription,Long productId, String productName,
			Double productPrice, Integer count) {
		Order order = new Order();
		order.setId(orderId);
		order.setCustomer(createCustomer(customerId, customerFirstName, customerLastName, customerEmail, customerDescription));
		order.setProduct(createProduct(productId, productPrice, productName));
		order.setCount(count);
		return order;
	}

	private Order createOrder (Long orderId, Customer customer, Product product, Integer count) {
		Order order = new Order();
		order.setId(orderId);
		order.setCustomer(customer);
		order.setProduct(product);
		order.setCount(count);
		return order;
	}

	private Customer createCustomer (Long customerId, String firstName, String lastName, String email, String description) {
		Customer customer = new Customer();
		customer.setId(customerId);
		customer.setFirstName(firstName);
		customer.setLastName(lastName);
		customer.setEmail(email);
		customer.setDescription(description);
		return customer;
	}

	private Product createProduct (Long productId, Double price, String name) {
		Product product = new Product();
		product.setId(productId);
		product.setPrice(price);
		product.setName(name);
		return product;
	}

	private OrderRequest createOrderRequest (Long customerId, Long productId, Integer count) {
		OrderRequest orderRequest = new OrderRequest();
		orderRequest.setCustomerId(customerId);
		orderRequest.setProductId(productId);
		orderRequest.setCount(count);
		return orderRequest;
	}

	private CustomerResponse createCustomerResponse (Long customerId, String firstName, String lastName, String email,
			String description) {
		CustomerResponse customerResponse = new CustomerResponse();
		customerResponse.setId(customerId);
		customerResponse.setFirstName(firstName);
		customerResponse.setLastName(lastName);
		customerResponse.setEmail(email);
		customerResponse.setDescription(description);
		return customerResponse;
	}

	private ProductResponse createProductResponse (Long productId, String name, Double price) {
		ProductResponse productResponse = new ProductResponse();
		productResponse.setId(productId);
		productResponse.setName(name);
		productResponse.setPrice(price);
		return productResponse;
	}
}
