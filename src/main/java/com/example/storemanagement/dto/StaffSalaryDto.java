package com.example.storemanagement.dto;

import com.example.storemanagement.enity.Staff;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffSalaryDto {
    private String staffName;
    private LocalDate startWork;
    private Double totalWorkTimeByMonth;

    private Integer totalWorkShift;

    private Integer termSalary;

}
