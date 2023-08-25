package com.example.storemanagement.repository;

import com.example.storemanagement.enity.Campaign;
import com.example.storemanagement.enity.Product;
import com.example.storemanagement.enity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign, Integer> {
    List<Campaign> findAllByStoreId(Integer storeId);

    @Query(value = "SELECT * from campaign WHERE store_id = ?1 AND start_date < ?2 AND end_date > ?2", nativeQuery = true)
    List<Campaign> findAllCampaignRunning(Integer storeId, LocalDateTime now);

    @Query(value = "SELECT * from campaign WHERE store_id = ?1 AND start_date = ?2", nativeQuery = true)
    List<Campaign> findAllCampaignByStartDate(Integer storeId, LocalDateTime startDate);
    @Query(value = "SELECT * from campaign WHERE store_id = ?1 AND end_date = ?2", nativeQuery = true)
    List<Campaign> findAllCampaignByEndDate(Integer storeId, LocalDateTime endDate);

    @Query(value = "SELECT * from campaign WHERE store_id = ?1 AND end_date < ?2", nativeQuery = true)
    List<Campaign> findAllCampaignExpired(Integer storeId, LocalDateTime now);

    @Query("SELECT c FROM Campaign c JOIN c.products p " +
            "WHERE p = :product " +
            "AND ((:startDate >= c.startDate AND :startDate <= c.endDate) " +
            "OR (:endDate >= c.startDate AND :endDate <= c.endDate) " +
            "OR (:startDate <= c.startDate AND :endDate >= c.endDate))")
    List<Campaign> findConflictingCampaigns(@Param("product") Product product,
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);
}
