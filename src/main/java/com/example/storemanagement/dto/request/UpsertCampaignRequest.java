package com.example.storemanagement.dto.request;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpsertCampaignRequest {
    private Integer campaignId;
    private String title;
    private Integer reducedPrice;
    private Integer reduceType;
    private Boolean isActive;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<Long> productId;
}
