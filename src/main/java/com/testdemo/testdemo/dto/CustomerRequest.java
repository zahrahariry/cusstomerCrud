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
public class CustomerRequest {

	private String description;

	@NotBlank
	private String email;

	private String firstName;

	private String lastName;

}
