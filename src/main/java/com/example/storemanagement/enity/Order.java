package com.example.storemanagement.enity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="orders")
public class Order {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "order_number", nullable = false)
    private String orderNumber;
    @Column(name = "product_name", nullable = false)
    private String productName;
    @Column(name = "group_product_name", nullable = false)
    private String groupProductName;
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    @Column(name = "discount")
    private Integer discount;
    @Column(name = "total_price", nullable = false)
    private Long totalPrice;
    @Column(name = "orderDate", columnDefinition = "TIMESTAMP")
    private LocalDateTime orderDate;
    @Column(name = "is_return")
    private Boolean isReturn;
    @Column(name = "return_created_by")
    private String returnCreatedBy;
    @Column(name = "return_created_at")
    private LocalDateTime returnCreatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "order")
    private List<UsedVoucher> usedVouchers = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        orderDate = LocalDateTime.now();
        isReturn = false;
    }
}
