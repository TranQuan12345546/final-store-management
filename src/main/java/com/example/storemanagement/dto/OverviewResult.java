package com.example.storemanagement.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OverviewResult {
    private Long revenue = 0L;

    private Double totalPricePercent;
    private Integer order = 0;

    private Double totalOrderPercent;
    private Long inventoryValue = 0L;

    private int product = 0;

    private int imports = 0;
    private int check = 0;

}
