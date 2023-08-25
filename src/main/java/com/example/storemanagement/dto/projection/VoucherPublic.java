package com.example.storemanagement.dto.projection;

import com.example.storemanagement.enity.Client;
import com.example.storemanagement.enity.Product;
import com.example.storemanagement.enity.Supplier;
import com.example.storemanagement.enity.Voucher;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface VoucherPublic {
    Integer getId();
    String getTitle();
    String getCode();
    Integer getOriginalQuantity();
    Integer getUsedQuantity();
    Integer getQuantityPerClient();
    Integer getReducedPrice();
    String getReduceType();
    Boolean getStatus();
    LocalDateTime getStartDate();
    LocalDateTime getEndDate();
    List<ProductPublic> getProducts();

    @RequiredArgsConstructor
    class VoucherPublicImpl implements VoucherPublic {
        private final Voucher voucher;

        @Override
        public Integer getId() {
            return this.voucher.getId();
        }

        @Override
        public String getTitle() {
            return this.voucher.getTitle();
        }

        @Override
        public String getCode() {
            return this.voucher.getCode();
        }

        @Override
        public Integer getOriginalQuantity() {
            return this.voucher.getOriginalQuantity();
        }

        @Override
        public Integer getUsedQuantity() {
            return this.voucher.getUsedQuantity();
        }

        @Override
        public Integer getQuantityPerClient() {
            return this.voucher.getQuantityPerClient();
        }

        @Override
        public Integer getReducedPrice() {
            return this.voucher.getReducedPrice();
        }

        @Override
        public String getReduceType() {
            return this.voucher.getReduceType().value;
        }

        @Override
        public Boolean getStatus() {
            return this.voucher.getStatus();
        }

        @Override
        public LocalDateTime getStartDate() {
            return this.voucher.getStartDate();
        }

        @Override
        public LocalDateTime getEndDate() {
            return this.voucher.getEndDate();
        }

        @Override
        public List<ProductPublic> getProducts() {
            List<ProductPublic> products = new ArrayList<>();
            for (Product product : this.voucher.getProducts()) {
                products.add(ProductPublic.of(product));
            }
            return products;
        }
    }

    static VoucherPublic of(Voucher voucher) {
        return new VoucherPublicImpl(voucher);
    }

}
