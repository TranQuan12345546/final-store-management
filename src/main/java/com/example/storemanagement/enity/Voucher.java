package com.example.storemanagement.enity;

import com.example.storemanagement.constant.ReduceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="voucher")
public class Voucher {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "title", columnDefinition = "VARCHAR(255)", nullable = false)
    private String title;

    @Column(name = "code", nullable = false)
    private String code;
    @Column(name = "original_quantity")
    private Integer originalQuantity;

    @Column(name = "used_quantity")
    private Integer usedQuantity;

    @Column(name = "quantity_per_client")
    private Integer quantityPerClient;

    @Column(name = "reduce_price")
    private Integer reducedPrice;

    @Column(name = "type_reduce")
    private ReduceType reduceType;

    @Column(name = "start_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime startDate;

    @Column(name = "end_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime endDate;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToMany
    @JoinTable(name = "voucher_product",
            joinColumns = @JoinColumn(name = "voucher_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "voucher")
    private List<UsedVoucher> usedVouchers = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        usedQuantity = 0;
        status = true;
        createdAt = LocalDateTime.now();
    }
}
