package com.example.storemanagement.enity;

import com.example.storemanagement.constant.Action;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="product_history")
public class ProductHistory {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer initialPrice;

    @Column(name = "change_quantity",nullable = false)
    private Integer changeQuantity;

    @Column(name = "final_quantity",nullable = false)
    private Integer finalQuantity;

    @Column(name = "action",nullable = false)
    private String action;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime updatedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "products_id")
    private Product product;

    @PrePersist
    public void prePersist() {
        updatedAt = LocalDateTime.now();
    }


}
