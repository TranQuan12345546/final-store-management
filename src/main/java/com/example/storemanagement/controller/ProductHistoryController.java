package com.example.storemanagement.controller;

import com.example.storemanagement.dto.projection.ProductHistoryPublic;
import com.example.storemanagement.service.ProductHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductHistoryController {
    @Autowired
    private ProductHistoryService productHistoryService;
    @Operation(summary = "Lấy lịch sử tác động lên sản phẩm theo id")
    @GetMapping("/product-history")
    public ResponseEntity<?> getProductHistory(@RequestParam Long productId) {
        List<ProductHistoryPublic> productHistory = productHistoryService.getProductHistory(productId);
        return ResponseEntity.ok(productHistory);
    }
}
