package com.example.storemanagement.dto.projection;

import com.example.storemanagement.enity.Supplier;
import lombok.RequiredArgsConstructor;

public interface SupplierPublic {
    Integer getId();
    String getName();
    String getEmail();
    String getPhone();
    String getAddress();


    @RequiredArgsConstructor
    class SupplierPublicImpl implements SupplierPublic {
        private final Supplier supplier;

        @Override
        public Integer getId() {
            return this.supplier.getId();
        }

        @Override
        public String getName() {
            return this.supplier.getName();
        }

        @Override
        public String getEmail() {
            return this.supplier.getEmail();
        }

        @Override
        public String getPhone() {
            return this.supplier.getPhone();
        }

        @Override
        public String getAddress() {
            return this.supplier.getAddress();
        }

    }

    static SupplierPublic of(Supplier supplier) {
        return new SupplierPublicImpl(supplier);
    }
}
