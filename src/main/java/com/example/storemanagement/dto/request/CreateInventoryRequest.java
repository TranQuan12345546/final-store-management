package com.example.storemanagement.dto.request;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateInventoryRequest {

    private String inventoryName;
    private List<Product> products;

    private Integer userId;
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Product {
        private Long productId;

        private Integer quantityBeforeCheck;

        private Integer quantityAfterCheck;
    }
}
