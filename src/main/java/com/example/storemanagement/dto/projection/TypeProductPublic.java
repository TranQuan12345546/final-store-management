package com.example.storemanagement.dto.projection;

import com.example.storemanagement.enity.Supplier;
import com.example.storemanagement.enity.TypeProduct;
import lombok.RequiredArgsConstructor;

public interface TypeProductPublic {
    Integer getId();
    String getName();
    @RequiredArgsConstructor
    class TypeProductPublicImpl implements TypeProductPublic {
        private final TypeProduct typeProduct;

        @Override
        public Integer getId() {
            return this.typeProduct.getId();
        }

        @Override
        public String getName() {
            if(this.typeProduct == null) {
                return "";
            }
            return this.typeProduct.getName();
        }
    }

    static TypeProductPublic of(TypeProduct typeProduct) {
        return new TypeProductPublicImpl(typeProduct);
    }
}
