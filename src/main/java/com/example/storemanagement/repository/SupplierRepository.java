package com.example.storemanagement.repository;

import com.example.storemanagement.enity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
    Optional<Supplier> findById(Integer id);

    Optional<Supplier> findByName(String name);

    List<Supplier> findAllByStoreId(Integer storeId);
}
