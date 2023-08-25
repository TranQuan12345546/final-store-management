package com.example.storemanagement.repository;

import com.example.storemanagement.enity.ShiftAssignment;
import com.example.storemanagement.enity.Store;
import com.example.storemanagement.enity.WorkShift;
import com.example.storemanagement.service.ShiftAssignmentService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ShiftAssignmentRepository extends JpaRepository<ShiftAssignment, Integer> {
    List<ShiftAssignment> findAllByWorkDayAndStoreId(LocalDate workDay, Integer storeId);

    @Query("SELECT sa FROM ShiftAssignment sa WHERE sa.workDay >= :startDate AND sa.workDay <= :endDate AND sa.store = :store ORDER BY sa.workShift.id ASC, sa.workDay ASC")
    List<ShiftAssignment> findAllByWorkDaysAndStore(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("store") Store store);

    ShiftAssignment findByWorkDayAndWorkShiftId(LocalDate workDay, Integer workShiftId);
}
