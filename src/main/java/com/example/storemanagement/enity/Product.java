package com.example.storemanagement.enity;

import com.example.storemanagement.constant.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name="product")
public class Product {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code_number", nullable = false)
    private String codeNumber;
    @Column(name = "name",columnDefinition = "VARCHAR(100)", nullable = false)
    private String name;
    @Column(name = "initial_price", nullable = false)
    private Integer initialPrice;
    @Column(name = "sale_price", nullable = false)
    private Integer salePrice;
    @Column(name = "promotional_price")
    private Integer promotionalPrice;
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
    @Column(name = "status", columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Column(name = "is_on_promotional")
    private Boolean isOnPromotional;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_product_id")
    private TypeProduct typeProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_product_id", nullable = false)
    private GroupProduct groupProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;
    @Column(name = "createdBy", columnDefinition = "TEXT")
    private String createdBy;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductHistory> productHistories = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InventoryProduct> inventoryProducts = new ArrayList<>();

    @ManyToMany(mappedBy = "products")
    private List<Campaign> campaigns = new ArrayList<>();

    @ManyToMany(mappedBy = "products")
    private List<Campaign> vouchers = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<UsedVoucher> usedVouchers = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        promotionalPrice = 0;
        isOnPromotional = false;
        createdAt = LocalDateTime.now();
    }


}
