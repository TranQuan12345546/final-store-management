package com.example.storemanagement.enity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="inventory_product")
public class InventoryProduct {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", columnDefinition = "VARCHAR(100)")
    private String productName;

    @Column(name = "code_number", columnDefinition = "VARCHAR(100)")
    private String codeNumber;

    @Column(name = "group_name", columnDefinition = "VARCHAR(100)")
    private String groupName;

    @Column(name = "initial_price", columnDefinition = "VARCHAR(100)")
    private Integer initialPrice;

    @Column(name = "quantity_before_check", nullable = false)
    private Integer quantityBeforeCheck;

    @Column(name = "quantity_after_check", nullable = false)
    private Integer quantityAfterCheck;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

}
