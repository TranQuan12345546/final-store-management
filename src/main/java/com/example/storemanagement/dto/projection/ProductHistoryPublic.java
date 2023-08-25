package com.example.storemanagement.dto.projection;

import com.example.storemanagement.enity.Product;
import com.example.storemanagement.enity.ProductHistory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

public interface ProductHistoryPublic {
    Long getId();

    Integer getPrice();

    Integer getChangeQuantity();

    Integer getFinalQuantity();

    String getAction();

    String getCreatedBy();

    LocalDateTime getUpdatedAt();

    String getProductName();

    @RequiredArgsConstructor
    class ProductHistoryPublicImpl implements ProductHistoryPublic {
        private final ProductHistory productHistory;
        @Override
        public Long getId() {
            return this.productHistory.getId();
        }

        @Override
        public Integer getPrice() {
            return this.productHistory.getInitialPrice();
        }

        @Override
        public Integer getChangeQuantity() {
            return this.productHistory.getChangeQuantity();
        }

        @Override
        public Integer getFinalQuantity() {
            return this.productHistory.getFinalQuantity();
        }

        @Override
        public String getAction() {
            return this.productHistory.getAction();
        }

        @Override
        public String getCreatedBy() {
            return this.productHistory.getCreatedBy();
        }

        @Override
        public LocalDateTime getUpdatedAt() {
            return this.productHistory.getUpdatedAt();
        }

        @Override
        public String getProductName() {
            return this.productHistory.getProduct().getName();
        }
    }

    static ProductHistoryPublic of(ProductHistory productHistory) {
        return new ProductHistoryPublicImpl(productHistory);
    }

}
