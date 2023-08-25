package com.example.storemanagement.dto.request;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReturnRequest {
    private List<Long> orderIds;
    private Integer userId;
}
