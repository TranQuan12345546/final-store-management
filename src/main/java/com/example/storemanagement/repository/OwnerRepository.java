package com.example.storemanagement.repository;

import com.example.storemanagement.dto.OwnerDto;
import com.example.storemanagement.enity.Owner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OwnerRepository extends JpaRepository<Owner, Integer> {
    Page<OwnerDto> findByStatusOrderByCreatedAtDesc(Pageable pageable, Boolean status);
}