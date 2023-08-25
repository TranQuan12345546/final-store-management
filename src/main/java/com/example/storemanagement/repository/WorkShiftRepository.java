package com.example.storemanagement.repository;

import com.example.storemanagement.enity.WorkShift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkShiftRepository extends JpaRepository<WorkShift, Integer> {
    List<WorkShift> findAllByStoreId(Integer storeId);
}
