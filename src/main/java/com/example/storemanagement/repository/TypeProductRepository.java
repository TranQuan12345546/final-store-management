package com.example.storemanagement.repository;

import com.example.storemanagement.enity.TypeProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeProductRepository extends JpaRepository<TypeProduct, Integer> {
    TypeProduct findByName(String name);
}
