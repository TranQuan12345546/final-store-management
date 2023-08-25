package com.example.storemanagement.dto.request;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpsertShiftAssignmentRequest {
    private Integer shiftAssignmentId;
    private LocalDate workDay;
    private Integer workShiftId;
    private List<Integer> staffIds;

    private Integer workDayIndex;
    private Integer workShiftIndex;
}
