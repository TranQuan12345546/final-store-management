package com.example.storemanagement.dto.request;

import com.example.storemanagement.enity.GroupProduct;
import com.example.storemanagement.enity.Supplier;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpsertProductRequest {
        private Long id;
        private String codeNumber;
        @NotEmpty(message = "Name không được để trống")
        private String name;
        @NotEmpty(message = "Giá vốn không được để trống")
        private Integer initialPrice;

        @NotEmpty(message = "Giá bán không được để trống")
        private Integer salePrice;
        @NotEmpty(message = "Giá bán không được để trống")
        private Integer quantity;

        private String description;
        private String note;
        private Supplier supplier;
        private String typeProduct;
        private GroupProduct groupProduct;
        @NotEmpty
        private Integer store;
        private Integer userId;
}
