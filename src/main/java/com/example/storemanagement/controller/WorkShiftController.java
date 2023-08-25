package com.example.storemanagement.controller;

import com.example.storemanagement.dto.projection.WorkShiftPublic;
import com.example.storemanagement.dto.request.UpsertWorkShiftRequest;
import com.example.storemanagement.service.WorkShiftService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/work-shift")
public class WorkShiftController {
    @Autowired
    private WorkShiftService workShiftService;
    @Operation(summary = "Tạo ca làm việc mới.")
    @PostMapping("/{storeId}/create-shift")
    public ResponseEntity<?> createWorkShift(@PathVariable Integer storeId,@RequestBody UpsertWorkShiftRequest upsertWorkShiftRequest) {
        WorkShiftPublic workShift = workShiftService.createWorkShift(storeId, upsertWorkShiftRequest);
        return ResponseEntity.ok(workShift);
    }
    @Operation(summary = "Cập nhật ca làm việc.")
    @PutMapping("/{storeId}/update-shift")
    public ResponseEntity<?> updateWorkShift(@PathVariable Integer storeId,@RequestBody UpsertWorkShiftRequest upsertWorkShiftRequest) {
        WorkShiftPublic workShiftPublic = workShiftService.updateWorkShift(storeId, upsertWorkShiftRequest);
        return ResponseEntity.ok(workShiftPublic);
    }

}
