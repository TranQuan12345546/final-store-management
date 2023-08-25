package com.example.storemanagement.dto.request;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpsertVoucherRequest {
    private Integer voucherId;
    private String title;
    private String code;
    private Integer originalQuantity;
    private Integer reducedPrice;
    private Integer quantityPerClient;
    private Integer reduceType;
    private Boolean status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<Long> productId;
}
