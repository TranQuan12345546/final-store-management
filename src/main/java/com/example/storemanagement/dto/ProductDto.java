package com.example.storemanagement.dto;

import com.example.storemanagement.enity.Image;
import com.example.storemanagement.enity.Product;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String codeNumber;
    private String name;
    private Integer initialPrice;
    private Integer salePrice;
    private Integer quantity;
    private String description;
    private String note;
    private String groupProduct;
    private String status;
    private LocalDateTime createdAt;

    private String supplier;

    private String createdBy;

    private List<Image> images;



    public static ProductDto toProductDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setCodeNumber(product.getCodeNumber());
        productDto.setName(product.getName());
        productDto.setInitialPrice(product.getInitialPrice());
        productDto.setSalePrice(product.getSalePrice());
        productDto.setQuantity(product.getQuantity());
        productDto.setDescription(product.getDescription());
        productDto.setNote(product.getNote());
        productDto.setStatus(product.getStatus().value);
        if (product.getGroupProduct() != null) {
            productDto.setGroupProduct(product.getGroupProduct().getName());
        }
        productDto.setCreatedAt(product.getCreatedAt());
        if (product.getSupplier() != null) {
            productDto.setSupplier(product.getSupplier().getName());
        }
        productDto.setCreatedBy(product.getCreatedBy());
        if (product.getImages().size() != 0) {
            productDto.setImages(product.getImages());
        }
        return productDto;
    }

}
