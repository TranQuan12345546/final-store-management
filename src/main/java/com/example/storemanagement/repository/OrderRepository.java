package com.example.storemanagement.repository;

import com.example.storemanagement.dto.projection.OrderPublic;
import com.example.storemanagement.dto.projection.ProductPublic;
import com.example.storemanagement.enity.Order;
import com.example.storemanagement.enity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByStoreIdAndIsReturnFalse(Integer storeId, Pageable pageable);
    Page<Order> findAllByStoreIdAndIsReturnTrue(Integer storeId, Pageable pageable);

    List<Order> findAllByStoreId(Integer storeId);
    List<Order> findAllByProductId(Long productId);

    Long countByStoreId(Integer storeId);

    @Query("SELECT COUNT(e) FROM Order e WHERE e.store = :store AND e.orderDate >= :startOfDay AND e.orderDate < :endOfDay")
    Optional<Integer> getTotalOrderCountForDay(@Param("store") Store store, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT SUM(e.totalPrice) FROM Order e WHERE e.isReturn = false")
    Long getTotalPriceSum();

    @Query("SELECT SUM(e.totalPrice) FROM Order e WHERE e.store = :store AND e.isReturn = false AND e.orderDate >= :startOfDay AND e.orderDate < :endOfDay")
    Optional<Long> getTotalPriceSumForDay(@Param("store") Store store , @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    Order findByOrderNumberAndStoreId(String orderNumber, Integer storeId);
}
