package com.example.storemanagement.repository;

import com.example.storemanagement.enity.ProductHistory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductHistoryRepository extends JpaRepository<ProductHistory, Integer> {
    List<ProductHistory> findAllByProductId(Long id, Sort sort);
}
