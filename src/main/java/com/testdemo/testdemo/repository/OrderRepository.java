package com.testdemo.testdemo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface OrderRepository extends JpaRepository<Order, Long>, QuerydslPredicateExecutor<Order> {

	List<Order> findByCustomerId(Long customerId);
	List<Order> findByProductId(Long productId);
}
