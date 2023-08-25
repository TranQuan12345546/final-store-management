package com.example.storemanagement.repository;

import com.example.storemanagement.enity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface SalaryRepository extends JpaRepository<Salary, Integer> {
    @Query("SELECT s FROM Salary s WHERE s.store.id = ?1 AND s.staff.id = ?2 AND s.workDay >= ?3 AND s.workDay <= ?4")
    List<Salary> findAllSalaryByStoreIdAndStaffIdAndDate(Integer storeId, Integer staffId, LocalDate startDate, LocalDate endDate);

}
