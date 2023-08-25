package com.example.storemanagement.controller;

import com.example.storemanagement.dto.StaffSalaryDto;
import com.example.storemanagement.enity.Salary;
import com.example.storemanagement.repository.SalaryRepository;
import com.example.storemanagement.service.SalaryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/salary")
public class SalaryController {
    @Autowired
    private SalaryService salaryService;

    @Operation(summary = "Lấy bảng lương nhân viên theo ngày nhập vào")
    @GetMapping("/{storeId}")
    public ResponseEntity<?> getSalaryStaffByDate(@PathVariable Integer storeId,@RequestParam LocalDate startDate,@RequestParam LocalDate endDate) {
        List<StaffSalaryDto> staffSalaryDto = salaryService.getAllSalaryByAllStaffByDate(storeId, startDate, endDate);
        return ResponseEntity.ok(staffSalaryDto);
    }
    @Operation(summary = "Thay đổi tiền lương nhân viên theo giờ")
    @PutMapping("/{storeId}/change-hourly-salary")
    public ResponseEntity<?> changeSalaryPerHour(@PathVariable Integer storeId, @RequestParam Integer salaryPerHour) {
        salaryService.changeSalaryPerHour(storeId, salaryPerHour);
        return ResponseEntity.ok("Update thành công");
    }
}
