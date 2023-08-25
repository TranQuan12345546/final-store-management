package com.example.storemanagement.repository;

import com.example.storemanagement.enity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    Voucher findByCodeAndStoreId(String code, Integer storeId);
    @Query(value = "SELECT * from voucher WHERE store_id = ?1 AND start_date < ?2 AND end_date > ?2", nativeQuery = true)
    List<Voucher> findALlVoucherByTime(Integer storeId, LocalDateTime now);

    List<Voucher> findAllByStoreId(Integer storeId);

    Voucher findByCode(String code);
}
