package com.example.storemanagement.dto.projection;

import com.example.storemanagement.enity.WorkShift;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

public interface WorkShiftPublic {
    Integer getId();
    String getName();
    LocalTime getStartShift();
    LocalTime getEndShift();

    Integer getOrderShift();

    @RequiredArgsConstructor
    class WorkShiftPublicImpl implements WorkShiftPublic {
        private final WorkShift workShift;

        @Override
        public Integer getId() {
            return this.workShift.getId();
        }

        @Override
        public String getName() {
            return this.workShift.getName();
        }

        @Override
        public LocalTime getStartShift() {
            return this.workShift.getStartShift();
        }

        @Override
        public LocalTime getEndShift() {
            return this.workShift.getEndShift();
        }

        @Override
        public Integer getOrderShift() {
            return this.workShift.getOrderShift();
        }
    }

    static WorkShiftPublic of(WorkShift workShift) {
        return new WorkShiftPublicImpl(workShift);
    }

}
