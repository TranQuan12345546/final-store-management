package com.example.storemanagement.dto.projection;

import com.example.storemanagement.enity.Inventory;
import com.example.storemanagement.enity.InventoryProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface InventoryPublic {
    Integer getId();

    String getInventoryName();

    String getCreatedBy();

    Integer getInventoryProductQuantity();

    LocalDateTime getCreatedAt();

    @RequiredArgsConstructor
    class InventoryPublicImpl implements InventoryPublic {

        private final Inventory inventory;
        @Override
        public Integer getId() {
            return this.inventory.getId();
        }

        @Override
        public String getInventoryName() {
            return this.inventory.getInventoryName();
        }

        @Override
        public String getCreatedBy() {
            return this.inventory.getCreatedBy();
        }

        @Override
        public Integer getInventoryProductQuantity() {
            if (this.inventory.getInventoryProducts() == null) {
                return 0;
            }
            return this.inventory.getInventoryProducts().size();
        }

        @Override
        public LocalDateTime getCreatedAt() {
            return this.inventory.getCreatedAt();
        }
    }

    static InventoryPublic of(Inventory inventory) {
        return new InventoryPublicImpl(inventory);
    }
}
