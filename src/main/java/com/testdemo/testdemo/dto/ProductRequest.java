package com.testdemo.testdemo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@RequiredArgsConstructor
public class ProductRequest {

	@NotBlank
	private String name;

	@NotBlank
	private Double price;
}
