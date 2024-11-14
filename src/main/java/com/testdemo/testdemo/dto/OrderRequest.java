package com.testdemo.testdemo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@RequiredArgsConstructor
public class OrderRequest {

	@NotNull
	private Long customerId;

	@NotNull
	private Long productId;

	@NotNull
	private Integer count;
}
