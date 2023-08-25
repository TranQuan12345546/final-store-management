package com.example.storemanagement.dto.projection;

import com.example.storemanagement.enity.Product;
import com.example.storemanagement.enity.ShiftAssignment;
import com.example.storemanagement.enity.Staff;
import com.example.storemanagement.enity.WorkShift;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public interface ShiftAssignmentPublic {
    Integer getId();
    LocalDate getWorkDay();
    Integer getWorkShiftId();
    List<String> getStaffNames();
    List<Integer> getStaffIds();
    Integer getWorkShiftIndex();
    Integer getWorkDayIndex();

    LocalTime getCheckInTime();
    LocalTime getCheckOutTime();

    @RequiredArgsConstructor
    class ShiftAssignmentPublicImpl implements ShiftAssignmentPublic {
        private final ShiftAssignment shiftAssignment;

        @Override
        public Integer getId() {
            return this.shiftAssignment.getId();
        }

        @Override
        public LocalDate getWorkDay() {
            return this.shiftAssignment.getWorkDay();
        }

        @Override
        public Integer getWorkShiftId() {
            return this.shiftAssignment.getWorkShift().getId();
        }

        @Override
        public List<String> getStaffNames() {
            List<String> staffIdList = new ArrayList<>();
            for (Staff staff : this.shiftAssignment.getStaffs()) {
                staffIdList.add(staff.getFullName());
            }
            return staffIdList;
        }

        @Override
        public List<Integer> getStaffIds() {
            List<Integer> staffIdList = new ArrayList<>();
            for (Staff staff : this.shiftAssignment.getStaffs()) {
                staffIdList.add(staff.getId());
            }
            return staffIdList;
        }

        @Override
        public Integer getWorkShiftIndex() {
            return this.shiftAssignment.getWorkShiftIndex();
        }

        @Override
        public Integer getWorkDayIndex() {
            return this.shiftAssignment.getWorkDayIndex();
        }

        @Override
        public LocalTime getCheckInTime() {
            return this.shiftAssignment.getCheckInTime();
        }

        @Override
        public LocalTime getCheckOutTime() {
            return this.shiftAssignment.getCheckOutTime();
        }
    }

    static ShiftAssignmentPublic of(ShiftAssignment shiftAssignment) {
        return new ShiftAssignmentPublicImpl(shiftAssignment);
    }
}
