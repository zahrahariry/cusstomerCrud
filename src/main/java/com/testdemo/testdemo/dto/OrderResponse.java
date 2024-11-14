package com.testdemo.testdemo.dto;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@RequiredArgsConstructor
public class OrderResponse {

	private Long id;

	private CustomerResponse customer;

	private ProductResponse product;

	private Integer count;
}
