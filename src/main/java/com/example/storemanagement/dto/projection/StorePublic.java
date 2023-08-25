package com.example.storemanagement.dto.projection;

import com.example.storemanagement.enity.GroupProduct;
import com.example.storemanagement.enity.Store;
import lombok.RequiredArgsConstructor;

public interface StorePublic {
    Integer getId();
    String getName();
    @RequiredArgsConstructor
    class StorePublicImpl implements StorePublic {
        private final Store store;

        @Override
        public Integer getId() {
            return this.store.getId();
        }

        @Override
        public String getName() {
            if(this.store == null) {
                return "";
            }
            return this.store.getName();
        }
    }

    static StorePublic of(Store store) {
        return new StorePublicImpl(store);
    }
}
