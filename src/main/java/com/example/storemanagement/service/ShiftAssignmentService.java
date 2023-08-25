package com.example.storemanagement.service;

import com.example.storemanagement.dto.projection.ShiftAssignmentPublic;
import com.example.storemanagement.dto.projection.WorkShiftPublic;
import com.example.storemanagement.dto.request.UpsertShiftAssignmentRequest;
import com.example.storemanagement.enity.*;
import com.example.storemanagement.exception.NotFoundException;
import com.example.storemanagement.repository.*;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ShiftAssignmentService {

    private final ShiftAssignmentRepository shiftAssignmentRepository;
    private final StoreRepository storeRepository;
    private final WorkShiftRepository workShiftRepository;
    private final StaffRepository staffRepository;
    private final SalaryRepository salaryRepository;

    public ShiftAssignmentService(ShiftAssignmentRepository shiftAssignmentRepository,
                                  StoreRepository storeRepository,
                                  WorkShiftRepository workShiftRepository,
                                  StaffRepository staffRepository,
                                  SalaryRepository salaryRepository) {
        this.shiftAssignmentRepository = shiftAssignmentRepository;
        this.storeRepository = storeRepository;
        this.workShiftRepository = workShiftRepository;
        this.staffRepository = staffRepository;
        this.salaryRepository = salaryRepository;
    }

    public ShiftAssignmentPublic createShiftAssignment(Integer storeId, UpsertShiftAssignmentRequest upsertShiftAssignmentRequest) {
        ShiftAssignment shiftAssignment = new ShiftAssignment();
        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy store");
        });
        shiftAssignment.setStore(store);
        shiftAssignment.setWorkDay(upsertShiftAssignmentRequest.getWorkDay());
        WorkShift workShift = workShiftRepository.findById(upsertShiftAssignmentRequest.getWorkShiftId()).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy ca làm việc");
        });
        ShiftAssignment shiftAssignment1 = shiftAssignmentRepository.findByWorkDayAndWorkShiftId(upsertShiftAssignmentRequest.getWorkDay(), upsertShiftAssignmentRequest.getWorkShiftId());
        if (shiftAssignment1 != null) {
            throw new DuplicateKeyException("Ca làm việc này đã tồn tại, không thể tạo mới");
        }
        shiftAssignment.setWorkDayIndex(upsertShiftAssignmentRequest.getWorkDayIndex());
        shiftAssignment.setWorkShiftIndex(upsertShiftAssignmentRequest.getWorkShiftIndex());
        shiftAssignment.setWorkShift(workShift);
        if (upsertShiftAssignmentRequest.getStaffIds().stream().anyMatch(Objects::nonNull)) {
            List<Staff> staffs = staffRepository.findAllById(upsertShiftAssignmentRequest.getStaffIds());
            if (staffs.size() != 0) {
                shiftAssignment.setStaffs(staffs);
            } else {
                throw new NotFoundException("Không tìm thấy nhân viên nào");
            }
        }

        shiftAssignmentRepository.save(shiftAssignment);
        return ShiftAssignmentPublic.of(shiftAssignment);
    }

    public List<ShiftAssignmentPublic> getAllShiftAssignmentInWeek(Integer storeId, LocalDate daySelected) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy store");
        });
        List<LocalDate> daysOfWeek = getDayOfWeek(daySelected);

        LocalDate startDate = Collections.min(daysOfWeek);
        LocalDate endDate = Collections.max(daysOfWeek);

        List<ShiftAssignment> shiftAssignments = shiftAssignmentRepository.findAllByWorkDaysAndStore(startDate, endDate, store);

        return shiftAssignments.stream().map(ShiftAssignmentPublic::of).collect(Collectors.toList());
    }

    public List<WorkShiftPublic> getWorkShiftByDateSelected(Integer storeId, LocalDate daySelected) {
        List<ShiftAssignmentPublic> shiftAssignmentPublics = this.getAllShiftAssignmentInWeek(storeId, daySelected);
        List<Integer> workShiftIdList = new ArrayList<>();
        for (ShiftAssignmentPublic shiftAssignmentPublic : shiftAssignmentPublics) {
            Integer workShiftId = shiftAssignmentPublic.getWorkShiftId();
            if (!workShiftIdList.contains(workShiftId)) {
                workShiftIdList.add(workShiftId);
            }
        }
        System.out.println(workShiftIdList);
        List<WorkShift> workShifts = workShiftRepository.findAllById(workShiftIdList);
        System.out.println(workShifts.size());
        return workShifts.stream().map(WorkShiftPublic::of).collect(Collectors.toList());
    }

    public List<LocalDate> getDayOfWeek(LocalDate daySelected) {

        List<LocalDate> daysOfWeek = new ArrayList<>();
        DayOfWeek firstDayOfWeek = DayOfWeek.MONDAY;


        for (int i = 0; i < DayOfWeek.values().length; i++) {
            DayOfWeek dayOfWeek = firstDayOfWeek.plus(i);
            LocalDate day = daySelected.with(dayOfWeek);

            daysOfWeek.add(day);
        }
        return daysOfWeek;
    }

    public ShiftAssignmentPublic updateShiftAssignment(UpsertShiftAssignmentRequest upsertShiftAssignmentRequest) {
        ShiftAssignment shiftAssignment = shiftAssignmentRepository.findById(upsertShiftAssignmentRequest.getShiftAssignmentId()).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy ca làm việc");
        });
        List<Staff> staffs = staffRepository.findAllById(upsertShiftAssignmentRequest.getStaffIds());
        if (staffs.size() != 0) {
            shiftAssignment.setStaffs(staffs);
            shiftAssignmentRepository.save(shiftAssignment);
        } else {
            throw new NotFoundException("Không tìm thấy nhân viên nào");
        }
        return ShiftAssignmentPublic.of(shiftAssignment);
    }


    public void workShiftCheckIn(LocalTime checkInTime, Integer shiftAssignmentId) {
        ShiftAssignment shiftAssignment = shiftAssignmentRepository.findById(shiftAssignmentId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy ca làm việc có id " + shiftAssignmentId);
        });
        shiftAssignment.setCheckInTime(checkInTime);
        shiftAssignmentRepository.save(shiftAssignment);
    }

    public void workShiftCheckOut(LocalTime checkOutTime, Integer shiftAssignmentId) {
        ShiftAssignment shiftAssignment = shiftAssignmentRepository.findById(shiftAssignmentId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy ca làm việc có id " + shiftAssignmentId);
        });
        shiftAssignment.setCheckOutTime(checkOutTime);
        shiftAssignmentRepository.save(shiftAssignment);
        for (Staff staff : shiftAssignment.getStaffs()) {
            Salary salary = new Salary();
            salary.setStaff(staff);
            salary.setWorkDay(shiftAssignment.getWorkDay());
            salary.setStore(shiftAssignment.getStore());
            salary.setWorkShift(shiftAssignment.getWorkShift());
            Duration duration = Duration.between(shiftAssignment.getCheckInTime(), shiftAssignment.getCheckOutTime());
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();
            double decimalHours = hours + ((double) minutes / 60);
            double roundedDecimalHours = Math.round(decimalHours * 100.0) / 100.0;
            salary.setWorkHour(roundedDecimalHours);
            salaryRepository.save(salary);
        }

    }
}
