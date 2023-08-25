package com.example.storemanagement.repository;

import com.example.storemanagement.enity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Integer> {
    List<Client> findAllByStoreId(Integer storeId);
}