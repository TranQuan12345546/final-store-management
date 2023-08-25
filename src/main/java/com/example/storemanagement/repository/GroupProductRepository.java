package com.example.storemanagement.repository;

import com.example.storemanagement.enity.GroupProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupProductRepository extends JpaRepository<GroupProduct, Integer> {
    Optional<GroupProduct> findByIdAndStoreId(Integer groupId, Integer storeId);

    Optional<GroupProduct> findByName(String groupProduct);

    List<GroupProduct> findAllByStoreId(Integer storeId);
}
