package com.example.storemanagement.repository;

import com.example.storemanagement.enity.SalaryPerHour;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryPerHourRepository extends JpaRepository<SalaryPerHour, Integer> {
    SalaryPerHour findByStoreId(Integer storeId);
}
