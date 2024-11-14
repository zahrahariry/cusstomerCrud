package com.testdemo.testdemo.dto;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@RequiredArgsConstructor
public class CustomerResponse {

	private Long id;

	private String description;

	private String email;

	private String firstName;

	private String lastName;
}
