package com.example.storemanagement.repository;

import com.example.storemanagement.enity.Staff;
import com.example.storemanagement.enity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StaffRepository extends JpaRepository<Staff, Integer> {
    List<Staff> findAllByStoreId(Integer storeId);

}
