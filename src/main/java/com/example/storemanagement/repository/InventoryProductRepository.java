package com.example.storemanagement.repository;

import com.example.storemanagement.enity.InventoryProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryProductRepository extends JpaRepository<InventoryProduct, Long> {
    List<InventoryProduct> findAllByInventoryId(Integer inventoryId);
}
