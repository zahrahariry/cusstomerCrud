package com.testdemo.testdemo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.testdemo.testdemo.dto.ProductRequest;
import com.testdemo.testdemo.dto.ProductResponse;
import com.testdemo.testdemo.exception.ResourceNotFoundException;
import com.testdemo.testdemo.mapper.ProductMapper;
import com.testdemo.testdemo.repository.Product;
import com.testdemo.testdemo.repository.ProductRepository;
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
public class ProductServiceTest {

	@Mock
	private ProductRepository productRepository;

	@Spy
	private ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

	@InjectMocks
	private ProductService productService;

	@Test
	void getAllProducts_success () {
		Product firstProduct = createProduct(1L, "name-1", 1d);

		Product secondProduct = createProduct(2L, "name-2", 2d);

		Product thirdProduct = createProduct(3L, "name-3", 3d);

		when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(firstProduct, secondProduct, thirdProduct)));

		List<ProductResponse> productResponses = productService.getAllProducts();

		assertThat(productResponses).isNotNull();
		assertThat(productResponses.size()).isEqualTo(3);
		ProductResponse productResponse = productResponses.get(1);
		assertThat(productResponse.getId()).isNotNull();
		assertThat(productResponse.getId()).isEqualTo(2L);
		assertThat(productResponse.getName()).isEqualTo("name-2");
		assertThat(productResponse.getPrice()).isEqualTo(2d);
	}

	@Test
	void findProductById_validInput_success () {
		Product product = createProduct(1L, "name-1", 1d);

		when(productRepository.findById(any())).thenReturn(Optional.of(product));

		ProductResponse productResponse = productService.findProductById(1L);

		assertThat(productResponse).isNotNull();
		assertThat(productResponse.getId()).isNotNull();
		assertThat(productResponse.getId()).isEqualTo(1L);
		assertThat(productResponse.getName()).isEqualTo("name-1");
		assertThat(productResponse.getPrice()).isEqualTo(1d);
	}

	@Test
	void findProductById_inValidInput_fail () {
		when(productRepository.findById(any())).thenReturn(Optional.empty());

		ProductResponse productResponse = productService.findProductById(1L);

		assertThat(productResponse).isNull();
	}

	@Test
	void updateProduct_validInput_success () {
		Product product = createProduct(1L, "name-1", 1d);

		when(productRepository.findById(any())).thenReturn(Optional.of(product));

		product.setName("name-updated");
		product.setPrice(1000d);

		when(productRepository.save(any())).thenReturn(product);

		ProductRequest productRequest = createProductRequest("name-updated", 1000d);

		ProductResponse productResponse = productService.updateProduct(1L, productRequest);

		assertThat(productResponse).isNotNull();
		assertThat(productResponse.getId()).isNotNull();
		assertThat(productResponse.getId()).isEqualTo(1L);
		assertThat(productResponse.getName()).isEqualTo("name-updated");
		assertThat(productResponse.getPrice()).isEqualTo(1000d);
	}

	@Test
	void updateProduct_inValidInput_fail () {

		when(productRepository.findById(any())).thenReturn(Optional.empty());

		ProductRequest productRequest = createProductRequest("name-updated", 1000d);

		assertThatThrownBy(() -> productService.updateProduct(1L, productRequest))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void deleteProduct_validInput_success () {
		Product product = createProduct(1L, "name-1", 1d);

		when(productRepository.findById(any())).thenReturn(Optional.of(product));
		willDoNothing().given(productRepository).deleteById(any());

		productService.deleteProduct(1L);

		verify(productRepository).deleteById(1L);

	}

	@Test
	void deleteProduct_inValidInput_fail () {

		when(productRepository.findById(any())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> productService.deleteProduct(1L))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void saveProduct_validInputs_success() {
		when(productRepository.save(any(Product.class))).thenReturn(createProduct(1L, "name-1", 1d));

		ProductResponse productResponse = productService.saveProduct(createProductRequest("name-1", 1d));

		assertThat(productResponse).isNotNull();
		assertThat(productResponse.getId()).isNotNull();
		assertThat(productResponse.getId()).isEqualTo(1L);
		assertThat(productResponse.getName()).isEqualTo("name-1");
		assertThat(productResponse.getPrice()).isEqualTo(1d);
	}



	private Product createProduct (Long id, String name, Double price) {
		Product product = new Product();
		product.setId(id);
		product.setName(name);
		product.setPrice(price);
		return product;
	}

	private ProductRequest createProductRequest (String name, Double price) {
		ProductRequest productRequest = new ProductRequest();
		productRequest.setPrice(price);
		productRequest.setName(name);
		return productRequest;
	}
}
