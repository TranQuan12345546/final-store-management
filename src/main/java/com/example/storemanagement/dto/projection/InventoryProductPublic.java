package com.example.storemanagement.dto.projection;

import com.example.storemanagement.enity.InventoryProduct;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

public interface InventoryProductPublic {
    Long getId();

    String getProductName();

    String getCodeNumber();

    String getGroupName();

    Integer getProductPrice();

    Integer getQuantityBeforeCheck();

    Integer getQuantityAfterCheck();

    LocalDateTime getCreatedAt();

    @RequiredArgsConstructor
    class InventoryProductPublicIml implements InventoryProductPublic {
        private final InventoryProduct inventoryProduct;
        @Override
        public Long getId() {
            return this.inventoryProduct.getId();
        }

        @Override
        public String getProductName() {
            return this.inventoryProduct.getProduct().getName();
        }

        @Override
        public String getCodeNumber() {
            return this.inventoryProduct.getCodeNumber();
        }

        @Override
        public String getGroupName() {
            return this.inventoryProduct.getGroupName();
        }


        @Override
        public Integer getProductPrice() {
            return this.inventoryProduct.getProduct().getSalePrice();
        }

        @Override
        public Integer getQuantityBeforeCheck() {
            return this.inventoryProduct.getQuantityBeforeCheck();
        }

        @Override
        public Integer getQuantityAfterCheck() {
            return this.inventoryProduct.getQuantityAfterCheck();
        }

        @Override
        public LocalDateTime getCreatedAt() {
            return this.inventoryProduct.getCreatedAt();
        }
    }

    static InventoryProductPublic of(InventoryProduct inventoryProduct) {
        return new InventoryProductPublicIml(inventoryProduct);
    }
}
