package com.example.storemanagement.repository;

import com.example.storemanagement.enity.Owner;
import com.example.storemanagement.enity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Integer> {
    List<Store> findByOwnerId(Integer ownerId);

    Optional<Owner> findOwnerById(Integer id);
}
