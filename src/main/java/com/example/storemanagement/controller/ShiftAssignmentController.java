package com.example.storemanagement.controller;

import com.example.storemanagement.dto.projection.ShiftAssignmentPublic;
import com.example.storemanagement.dto.projection.WorkShiftPublic;
import com.example.storemanagement.dto.request.UpsertShiftAssignmentRequest;
import com.example.storemanagement.enity.ShiftAssignment;
import com.example.storemanagement.service.ShiftAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/shift-assignment")
public class ShiftAssignmentController {
    @Autowired
    private ShiftAssignmentService shiftAssignmentService;

    @Operation(summary = "Tạo phân ca làm việc")
    @PostMapping("/{storeId}/create")
    public ResponseEntity<?> createShiftAssignment(@PathVariable Integer storeId, @RequestBody List<UpsertShiftAssignmentRequest> upsertShiftAssignmentRequestList) {
        List<ShiftAssignmentPublic> shiftAssignmentPublics = new ArrayList<>();
        for (UpsertShiftAssignmentRequest upsertShiftAssignmentRequest : upsertShiftAssignmentRequestList) {
            ShiftAssignmentPublic shiftAssignmentPublic = shiftAssignmentService.createShiftAssignment(storeId, upsertShiftAssignmentRequest);
            shiftAssignmentPublics.add(shiftAssignmentPublic);
        }
        return ResponseEntity.ok(shiftAssignmentPublics);
    }

    @Operation(summary = "Cập nhật phân ca làm việc")
    @PutMapping("/update")
    public ResponseEntity<?> updateShiftAssignment(@RequestBody List<UpsertShiftAssignmentRequest> upsertShiftAssignmentRequestList) {
        List<ShiftAssignmentPublic> shiftAssignmentPublics = new ArrayList<>();
        for (UpsertShiftAssignmentRequest upsertShiftAssignmentRequest : upsertShiftAssignmentRequestList) {
            ShiftAssignmentPublic shiftAssignmentPublic = shiftAssignmentService.updateShiftAssignment(upsertShiftAssignmentRequest);
            shiftAssignmentPublics.add(shiftAssignmentPublic);
        }
        return ResponseEntity.ok(shiftAssignmentPublics);
    }

    @Operation(summary = "Lấy ra các ngày trong tuần")
    @GetMapping("/get-day-of-week")
    public ResponseEntity<?> getDayOfWeek(@RequestParam LocalDate daySelected) {
        List<LocalDate> dayOfWeek = shiftAssignmentService.getDayOfWeek(daySelected);
        return ResponseEntity.ok(dayOfWeek);
    }

    @Operation(summary = "Lấy thông tin phân ca làm việc trong tuần")
    @GetMapping("/{storeId}/get-shift-assignment")
    public ResponseEntity<?> getShiftAssignment(@PathVariable Integer storeId, @RequestParam LocalDate daySelected) {
        List<ShiftAssignmentPublic> shiftAssignmentPublics = shiftAssignmentService.getAllShiftAssignmentInWeek(storeId, daySelected);
        return ResponseEntity.ok(shiftAssignmentPublics);

    }

    @Operation(summary = "Lấy thông tin ca làm việc theo ngày được chọn")
    @GetMapping("/{storeId}/get-work-shift")
    public ResponseEntity<?> getWorkShiftByDateSelected(@PathVariable Integer storeId, @RequestParam LocalDate daySelected) {
        List<WorkShiftPublic> workShiftPublics = shiftAssignmentService.getWorkShiftByDateSelected(storeId, daySelected);
        return ResponseEntity.ok(workShiftPublics);
    }

    @Operation(summary = "Check in ca làm việc")
    @PutMapping("/{shiftAssignmentId}/work-shift-check-in")
    public ResponseEntity<?> workShiftCheckIn(@RequestParam LocalTime checkInTime,
                                              @PathVariable Integer shiftAssignmentId) {
        shiftAssignmentService.workShiftCheckIn(checkInTime, shiftAssignmentId);
        return ResponseEntity.ok("");
    }
    @Operation(summary = "Check out ca làm việc")
    @PutMapping("/{shiftAssignmentId}/work-shift-check-out")
    public ResponseEntity<?> workShiftCheckOut(@RequestParam LocalTime checkOutTime,
                                              @PathVariable Integer shiftAssignmentId) {
        shiftAssignmentService.workShiftCheckOut(checkOutTime, shiftAssignmentId);
        return ResponseEntity.ok("");
    }
}
