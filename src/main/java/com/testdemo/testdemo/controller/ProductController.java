package com.testdemo.testdemo.controller;

import java.util.List;

import com.testdemo.testdemo.dto.ProductRequest;
import com.testdemo.testdemo.dto.ProductResponse;
import com.testdemo.testdemo.service.ProductService;
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
@RequestMapping("/product")
@RequiredArgsConstructor
@Tag(name = "product Controller", description = "Product management APIs")
public class ProductController {

	private final ProductService productService;

	@GetMapping
	@Operation(summary = "get all products")
	public ResponseEntity<List<ProductResponse>> getAllProducts () {
		log.info("going to get all products");
		return ResponseEntity.ok(productService.getAllProducts());
	}

	@GetMapping(path = "/{productId}")
	@Operation(summary = "get product by productId")
	public ResponseEntity<ProductResponse> getProductById (@PathVariable(name = "productId") Long productId) {
		log.info("going to get product by id : {}", productId);
		return ResponseEntity.ok(productService.findProductById(productId));
	}

	@PostMapping
	@Operation(summary = "save product")
	public ResponseEntity<ProductResponse> saveProduct (@RequestBody @Valid ProductRequest productRequest) {
		log.info("going to save product request : {}", productRequest);
		return ResponseEntity.ok(productService.saveProduct(productRequest));
	}

	@PutMapping(path = "/{productId}")
	@Operation(summary = "update product")
	public ResponseEntity<ProductResponse> updateProduct (@PathVariable(name = "productId") Long productId, @RequestBody @Valid ProductRequest productRequest) {
		log.info("going to update product by id : {} and product request : {}", productId, productRequest);
		return ResponseEntity.ok(productService.updateProduct(productId, productRequest));
	}

	@DeleteMapping(path = "/{productId}")
	@Operation(summary = "delete product")
	public ResponseEntity<Void> deleteProduct (@PathVariable(name = "productId") Long productId) {
		log.info("going to delete product by id : {}", productId);
		productService.deleteProduct(productId);
		return ResponseEntity.ok().build();
	}
}
