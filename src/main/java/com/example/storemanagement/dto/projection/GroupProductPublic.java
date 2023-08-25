package com.example.storemanagement.dto.projection;

import com.example.storemanagement.enity.GroupProduct;
import lombok.RequiredArgsConstructor;

public interface GroupProductPublic {
    Integer getId();
    String getName();
    @RequiredArgsConstructor
    class GroupProductPublicImpl implements GroupProductPublic {
        private final GroupProduct groupProduct;

        @Override
        public Integer getId() {
            return this.groupProduct.getId();
        }

        @Override
        public String getName() {
            return this.groupProduct.getName();
        }
    }

    static GroupProductPublic of(GroupProduct groupProduct) {
        return new GroupProductPublicImpl(groupProduct);
    }
}
