package com.example.storemanagement.controller;

import com.example.storemanagement.dto.projection.ShiftAssignmentPublic;
import com.example.storemanagement.dto.projection.WorkShiftPublic;
import com.example.storemanagement.dto.request.UpsertShiftAssignmentRequest;
import com.example.storemanagement.enity.ShiftAssignment;
import com.example.storemanagement.service.ShiftAssignmentService;
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


    @PostMapping("/{storeId}/create")
    public ResponseEntity<?> createShiftAssignment(@PathVariable Integer storeId, @RequestBody List<UpsertShiftAssignmentRequest> upsertShiftAssignmentRequestList) {
        List<ShiftAssignmentPublic> shiftAssignmentPublics = new ArrayList<>();
        for (UpsertShiftAssignmentRequest upsertShiftAssignmentRequest : upsertShiftAssignmentRequestList) {
            ShiftAssignmentPublic shiftAssignmentPublic = shiftAssignmentService.createShiftAssignment(storeId, upsertShiftAssignmentRequest);
            shiftAssignmentPublics.add(shiftAssignmentPublic);
        }
        return ResponseEntity.ok(shiftAssignmentPublics);
    }


    @PutMapping("/update")
    public ResponseEntity<?> updateShiftAssignment(@RequestBody List<UpsertShiftAssignmentRequest> upsertShiftAssignmentRequestList) {
        List<ShiftAssignmentPublic> shiftAssignmentPublics = new ArrayList<>();
        for (UpsertShiftAssignmentRequest upsertShiftAssignmentRequest : upsertShiftAssignmentRequestList) {
            ShiftAssignmentPublic shiftAssignmentPublic = shiftAssignmentService.updateShiftAssignment(upsertShiftAssignmentRequest);
            shiftAssignmentPublics.add(shiftAssignmentPublic);
        }
        return ResponseEntity.ok(shiftAssignmentPublics);
    }

    @GetMapping("/get-day-of-week")
    public ResponseEntity<?> getDayOfWeek(@RequestParam LocalDate daySelected) {
        List<LocalDate> dayOfWeek = shiftAssignmentService.getDayOfWeek(daySelected);
        return ResponseEntity.ok(dayOfWeek);
    }

    @GetMapping("/{storeId}/get-shift-assignment")
    public ResponseEntity<?> getShiftAssignment(@PathVariable Integer storeId, @RequestParam LocalDate daySelected) {
        List<ShiftAssignmentPublic> shiftAssignmentPublics = shiftAssignmentService.getAllShiftAssignmentInWeek(storeId, daySelected);
        return ResponseEntity.ok(shiftAssignmentPublics);

    }

    @GetMapping("/{storeId}/get-work-shift")
    public ResponseEntity<?> getWorkShiftByDateSelected(@PathVariable Integer storeId, @RequestParam LocalDate daySelected) {
        List<WorkShiftPublic> workShiftPublics = shiftAssignmentService.getWorkShiftByDateSelected(storeId, daySelected);
        return ResponseEntity.ok(workShiftPublics);
    }

    @PutMapping("/{shiftAssignmentId}/work-shift-check-in")
    public ResponseEntity<?> workShiftCheckIn(@RequestParam LocalTime checkInTime,
                                              @PathVariable Integer shiftAssignmentId) {
        shiftAssignmentService.workShiftCheckIn(checkInTime, shiftAssignmentId);
        return ResponseEntity.ok("");
    }

    @PutMapping("/{shiftAssignmentId}/work-shift-check-out")
    public ResponseEntity<?> workShiftCheckOut(@RequestParam LocalTime checkOutTime,
                                              @PathVariable Integer shiftAssignmentId) {
        shiftAssignmentService.workShiftCheckOut(checkOutTime, shiftAssignmentId);
        return ResponseEntity.ok("");
    }
}
