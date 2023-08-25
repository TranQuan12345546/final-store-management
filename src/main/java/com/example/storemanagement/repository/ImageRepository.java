package com.example.storemanagement.repository;

import com.example.storemanagement.enity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Integer> {
    List<Image> findAllByProductId(Long productId);
}
