package com.testdemo.testdemo;


import java.util.List;

import com.testdemo.testdemo.dto.ProductRequest;
import com.testdemo.testdemo.dto.ProductResponse;
import com.testdemo.testdemo.mapper.ProductMapper;
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
public class ProductControllerIT {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ProductMapper productMapper;

	@BeforeEach
	void setUp() {
		productRepository.deleteAll();
	}

	@Test
	void saveProduct_validInput_success () {

		ResponseEntity<ProductResponse> productResponse = restTemplate.postForEntity(
				createURLWithPort("/product"),createProductRequest("test-name", 66d), ProductResponse.class
		);

		assertThat(productResponse).isNotNull();
		assertThat(productResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		ProductResponse productResponseBody = productResponse.getBody();
		assertThat(productResponseBody).isNotNull();
		assertThat(productResponseBody.getId()).isNotNull();
		assertThat(productResponseBody.getName()).isEqualTo("test-name");
		assertThat(productResponseBody.getPrice()).isEqualTo(66d);
	}

	@Test
	void getAllProducts_success () {
		createProduct("first-name-1", 1d);
		createProduct("first-name-2", 2d);
		createProduct("first-name-3", 3d);

		ResponseEntity<List<ProductResponse>> productResponseList = restTemplate.exchange(
				createURLWithPort("/product"),
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<ProductResponse>>() {}
		);

		assertThat(productResponseList).isNotNull();
		assertThat(productResponseList.getStatusCode()).isEqualTo(HttpStatus.OK);
		List<ProductResponse> productResponseBody = productResponseList.getBody();
		assertThat(productResponseBody).isNotNull();
		assertThat(productResponseBody.size()).isEqualTo(3);
		assertThat(productResponseBody.get(0).getName()).isEqualTo("first-name-1");
		assertThat(productResponseBody.get(0).getId()).isNotNull();
		assertThat(productResponseBody.get(1).getName()).isEqualTo("first-name-2");
		assertThat(productResponseBody.get(1).getId()).isNotNull();
		assertThat(productResponseBody.get(2).getName()).isEqualTo("first-name-3");
		assertThat(productResponseBody.get(2).getId()).isNotNull();
	}

	@Test
	void getProductById_success () {
		createProduct("first-name-1", 5d);
		Long id = createProduct("first-name-2", 7d);
		createProduct("first-name-3", 9d);

		ResponseEntity<ProductResponse> productResponse= restTemplate.exchange(
				createURLWithPort("/product/"+ id.toString()),
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<ProductResponse>() {}
		);

		assertThat(productResponse).isNotNull();
		assertThat(productResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		ProductResponse productResponseBody = productResponse.getBody();
		assertThat(productResponseBody).isNotNull();
		assertThat(productResponseBody.getName()).isEqualTo("first-name-2");
		assertThat(productResponseBody.getId()).isNotNull();
		assertThat(productResponseBody.getPrice()).isEqualTo(7d);
	}

	@Test
	void deleteProduct_validInput_success () {
		createProduct( "first-name-1", 1d);
		Long id = createProduct( "first-name-2", 2d);
		createProduct( "first-name-3", 3d);


		ResponseEntity<Void> productResponse = restTemplate.exchange(
				createURLWithPort("/product/"+ id.toString()),
				HttpMethod.DELETE,
				null,
				Void.class
		);

		assertThat(productResponse).isNotNull();
		assertThat(productResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		List<Product> remainingProduct = productRepository.findAll();
		assertThat(remainingProduct.size()).isEqualTo(2);
		assertThat(remainingProduct.get(0).getName()).isEqualTo("first-name-1");
		assertThat(remainingProduct.get(1).getName()).isEqualTo("first-name-3");
	}

	@Test
	void deleteProduct_inValidInput_fail () {
		createProduct("first-name-1", 1d);
		createProduct("first-name-2", 2d);
		createProduct("first-name-3", 3d);

		ResponseEntity<Void> productResponse = restTemplate.exchange(
				createURLWithPort("/product/95"),
				HttpMethod.DELETE,
				null,
				Void.class
		);

		assertThat(productResponse).isNotNull();
		assertThat(productResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		List<Product> remainingProduct = productRepository.findAll();
		assertThat(remainingProduct.size()).isEqualTo(3);
		assertThat(remainingProduct.get(0).getName()).isEqualTo("first-name-1");
		assertThat(remainingProduct.get(1).getName()).isEqualTo("first-name-2");
		assertThat(remainingProduct.get(2).getName()).isEqualTo("first-name-3");
	}

	@Test
	void updateProduct_validInput_success () {
		createProduct( "first-name-1", 1d);
		Long id = createProduct( "first-name-2", 2d);
		createProduct( "first-name-3", 3d);

		ProductRequest productRequest = createProductRequest("first-name-6", 6d);

		HttpEntity<ProductRequest> requestEntity = new HttpEntity<>(productRequest);

		ResponseEntity<ProductResponse> productResponse = restTemplate.exchange(
				createURLWithPort("/product/"+ id.toString()),
				HttpMethod.PUT,
				requestEntity,
				ProductResponse.class
		);

		assertThat(productResponse).isNotNull();
		assertThat(productResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		ProductResponse productResponseBody = productResponse.getBody();
		assertThat(productResponseBody).isNotNull();
		assertThat(productResponseBody.getName()).isEqualTo("first-name-6");
		assertThat(productResponseBody.getId()).isNotNull();
		assertThat(productResponseBody.getPrice()).isEqualTo(6d);
	}

	@Test
	void updateProduct_inValidInput_fail () {
		createProduct( "first-name-1", 1d);
		createProduct( "first-name-2", 2d);
		createProduct( "first-name-3", 3d);

		ProductRequest productRequest = createProductRequest("first-name-6", 6d);

		HttpEntity<ProductRequest> requestEntity = new HttpEntity<>(productRequest);

		ResponseEntity<ProductResponse> productResponse = restTemplate.exchange(
				createURLWithPort("/product/44"),
				HttpMethod.PUT,
				requestEntity,
				ProductResponse.class
		);

		assertThat(productResponse).isNotNull();
		assertThat(productResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void saveProduct_inValidInput_fail () {

		ResponseEntity<ProductResponse> productResponse = restTemplate.postForEntity(
				createURLWithPort("/product"),null, ProductResponse.class
		);

		assertThat(productResponse).isNotNull();
		assertThat(productResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		ProductResponse productResponseBody = productResponse.getBody();
		assertThat(productResponseBody).isNotNull();
		assertThat(productResponseBody.getId()).isNull();
		assertThat(productResponseBody.getName()).isNull();
		assertThat(productResponseBody.getPrice()).isNull();
	}

	private ProductRequest createProductRequest (String name, Double price) {
		ProductRequest productRequest = new ProductRequest();
		productRequest.setName(name);
		productRequest.setPrice(price);
		return productRequest;
	}

	private Long createProduct (String name, Double price) {
		Product product = new Product();
		product.setName(name);
		product.setPrice(price);
		Product savedProduct = productRepository.save(product);
		return savedProduct.getId();
	}

	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}
}
