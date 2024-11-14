package com.testdemo.testdemo.dto;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@RequiredArgsConstructor
public class ProductResponse {

	private Long id;

	private String name;

	private Double price;
}
