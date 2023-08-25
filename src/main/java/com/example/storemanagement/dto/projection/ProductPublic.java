package com.example.storemanagement.dto.projection;

import com.example.storemanagement.enity.Image;
import com.example.storemanagement.enity.Product;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface ProductPublic {
    Long getId();
    String getCodeNumber();
    String getName();
    Integer getInitialPrice();
    Integer getSalePrice();

    Integer getPromotionalPrice();

    Boolean getIsOnPromotional();

    Integer getQuantity();

    String getDescription();

    String getNote();

    String getStatus();

    LocalDateTime getCreatedAt();

    SupplierPublic getSupplier();

    TypeProductPublic getTypeProduct();

    GroupProductPublic getGroupProduct();

    StorePublic getStore();

    String getCreatedBy();

    List<ImagePublic> getImages();

    @RequiredArgsConstructor
    class ProductPublicImpl implements ProductPublic {
         private final Product product;

        @Override
        public Long getId() {
            return this.product.getId();
        }

        @Override
        public String getCodeNumber() {
            return this.product.getCodeNumber();
        }

        @Override
        public String getName() {
            return this.product.getName();
        }

        @Override
        public Integer getInitialPrice() {
            return this.product.getInitialPrice();
        }

        @Override
        public Integer getSalePrice() {
            return this.product.getSalePrice();
        }

        @Override
        public Integer getPromotionalPrice() {
            return this.product.getPromotionalPrice();
        }

        @Override
        public Boolean getIsOnPromotional() {
            return this.product.getIsOnPromotional();
        }

        @Override
        public Integer getQuantity() {
            return this.product.getQuantity();
        }

        @Override
        public String getDescription() {
            return this.product.getDescription();
        }

        @Override
        public String getNote() {
            return this.product.getNote();
        }

        @Override
        public String getStatus() {
            return this.product.getStatus().value;
        }

        @Override
        public LocalDateTime getCreatedAt() {
            return this.product.getCreatedAt();
        }

        @Override
        public SupplierPublic getSupplier() {
            if(this.product.getSupplier() == null) {
                return null;
            }
            return SupplierPublic.of(this.product.getSupplier());
        }

        @Override
        public TypeProductPublic getTypeProduct() {
            if(this.product.getTypeProduct() == null) {
                return null;
            }
            return TypeProductPublic.of(this.product.getTypeProduct());
        }

        @Override
        public GroupProductPublic getGroupProduct() {
            if(this.product.getGroupProduct() == null) {
                return null;
            }
            return GroupProductPublic.of(this.product.getGroupProduct());
        }

        @Override
        public StorePublic getStore() {
            if(this.product.getStore() == null) {
                return null;
            }
            return StorePublic.of(this.product.getStore());
        }

        @Override
        public String getCreatedBy() {
            return this.product.getCreatedBy();
        }

        @Override
        public List<ImagePublic> getImages() {
            if(this.product.getImages() == null) {
                return null;
            }
            List<ImagePublic> imagePublics = new ArrayList<>();
            for (Image image : this.product.getImages()) {
                imagePublics.add(ImagePublic.of(image));
            }
            return imagePublics;
        }
    }

    static ProductPublic of(Product product) {
        return new ProductPublicImpl(product);
    }

}
