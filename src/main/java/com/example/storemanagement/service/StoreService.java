package com.example.storemanagement.service;

import com.example.storemanagement.enity.Store;
import com.example.storemanagement.enity.User;
import com.example.storemanagement.repository.StoreRepository;
import com.example.storemanagement.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    public StoreService(StoreRepository storeRepository,
                        UserRepository userRepository) {
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
    }

    public List<Store> getAllStore() {
        List<Store> stores = storeRepository.findAll();
        return stores;
    }
}
