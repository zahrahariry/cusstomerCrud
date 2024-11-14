package com.testdemo.testdemo.mapper;

import java.util.List;

import com.testdemo.testdemo.dto.ProductRequest;
import com.testdemo.testdemo.dto.ProductResponse;
import com.testdemo.testdemo.repository.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

	ProductResponse toProductResponse (Product product);

	List<ProductResponse> toProductResponseList (List<Product> products);

	Product toProduct (ProductRequest productRequest);

	Product toProduct (ProductResponse productResponse);
}
