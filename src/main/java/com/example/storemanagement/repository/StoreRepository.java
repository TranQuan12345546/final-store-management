package com.example.storemanagement.repository;

import com.example.storemanagement.enity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Integer> {
    List<Store> findByOwnerId(Integer ownerId);
}
