package com.example.storemanagement.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpsertWorkShiftRequest {
    private Integer workShiftId;
    private String name;
    private LocalTime startShift;
    private LocalTime endShift;
    private Integer orderShift;
}
