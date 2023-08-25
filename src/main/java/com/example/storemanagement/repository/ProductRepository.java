package com.example.storemanagement.repository;

import com.example.storemanagement.dto.projection.ProductPublic;
import com.example.storemanagement.enity.Campaign;
import com.example.storemanagement.enity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByCodeNumberAndStoreId(String generatedCodeNumber, Integer storeId);

    Long countByStoreId(Integer storeId);

    Page<Product> findByStoreIdAndGroupProductId(Integer storeId, Integer groupProductId, Pageable pageable);

    Page<Product> findByStoreId(Integer storeId, Pageable pageable);

    List<Product> findByStoreId(Integer storeId);

    List<ProductPublic> findAllByStoreId(Integer storeId);

    List<ProductPublic> findAllByStoreIdAndGroupProductId(Integer storeId, Integer groupId);

    @Query(value = "SELECT SUM(p.quantity) FROM product p WHERE p.store_id = :storeId", nativeQuery = true)
    Integer countAvailableProductsByStoreId(@Param("storeId") Integer storeId);

    @Query(value = "SELECT SUM(p.initial_price * p.quantity) FROM product p WHERE p.store_id = :storeId", nativeQuery = true)
    Long getTotalValueByStoreId(@Param("storeId") Integer storeId);

    @Query("SELECT c FROM Campaign c JOIN c.products p WHERE p.id = :productId AND c.store.id = :storeId")
    List<Campaign> getAllCampaigns(@Param("storeId") Integer storeId, @Param("productId") Long productId);

}
