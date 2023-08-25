package com.example.storemanagement.repository;

import com.example.storemanagement.enity.Inventory;
import com.example.storemanagement.enity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    @Query("SELECT e FROM Inventory e WHERE e.store = :store AND e.createdAt >= :startOfDay AND e.createdAt < :endOfDay")
    List<Inventory> findAllByStoreIdAAndCreatedAt(@Param("store") Store store, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.store.id = :storeId AND i.createdAt >= :startOfDay AND i.createdAt <= :endOfDay")
    Integer countByDay(Integer storeId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
