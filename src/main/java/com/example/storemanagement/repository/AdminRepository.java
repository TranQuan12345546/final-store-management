package com.example.storemanagement.repository;

import com.example.storemanagement.enity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Admin findByUsername(String username);

}
