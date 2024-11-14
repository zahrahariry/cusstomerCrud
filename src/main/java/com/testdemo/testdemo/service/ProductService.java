package com.testdemo.testdemo.service;

import java.util.List;
import java.util.Optional;

import com.testdemo.testdemo.dto.ProductRequest;
import com.testdemo.testdemo.dto.ProductResponse;
import com.testdemo.testdemo.exception.ResourceNotFoundException;
import com.testdemo.testdemo.mapper.ProductMapper;
import com.testdemo.testdemo.repository.Product;
import com.testdemo.testdemo.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;

	private final ProductMapper productMapper;

	public List<ProductResponse> getAllProducts () {
		return productMapper.toProductResponseList(productRepository.findAll());
	}

	public ProductResponse findProductById (Long productId) {
		return Optional.ofNullable(getProductById(productId))
				.map(productMapper::toProductResponse)
				.orElse(null);
	}

	public ProductResponse saveProduct (ProductRequest productRequest) {
		return productMapper.toProductResponse(productRepository.save(productMapper.toProduct(productRequest)));
	}

	public ProductResponse updateProduct (Long productId, ProductRequest productRequest) {
		return Optional.ofNullable(getProductById(productId))
				.map(existingProduct -> saveProduct(productRequest))
				.orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
	}

	public void deleteProduct (Long productId) {
		Optional.ofNullable(getProductById(productId))
				.ifPresentOrElse(
						existingProduct -> {
							productRepository.deleteById(productId);
						},
						() -> {
							log.error("there is not product with id : {}", productId);
							throw new ResourceNotFoundException("product not found with id : " + productId.toString());
						}
				);
	}

	private Product getProductById (Long productId) {
		return productRepository.findById(productId).orElse(null);
	}
}
