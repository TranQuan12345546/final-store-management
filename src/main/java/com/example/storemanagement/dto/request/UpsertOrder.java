package com.example.storemanagement.dto.request;

import com.example.storemanagement.enity.Product;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpsertOrder {

    private List<ProductItem> products;

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ProductItem {

        private Integer userId;
        private Integer clientId;
        @NotNull
        private Long productId;
        @NotNull
        private Integer quantity;
        @NotNull
        private Integer discount;
        @NotNull
        private Long totalPrice;

        private String voucherCode;

    }

}
