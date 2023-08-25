package com.example.storemanagement.service;

import com.example.storemanagement.dto.StaffSalaryDto;
import com.example.storemanagement.enity.Salary;
import com.example.storemanagement.enity.SalaryPerHour;
import com.example.storemanagement.enity.Staff;
import com.example.storemanagement.repository.SalaryPerHourRepository;
import com.example.storemanagement.repository.SalaryRepository;
import com.example.storemanagement.repository.StaffRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class SalaryService {

    private final SalaryRepository salaryRepository;
    private final StaffRepository staffRepository;
    private final SalaryPerHourRepository salaryPerHourRepository;

    public SalaryService(SalaryRepository salaryRepository,
                         StaffRepository staffRepository,
                         SalaryPerHourRepository salaryPerHourRepository) {
        this.salaryRepository = salaryRepository;
        this.staffRepository = staffRepository;
        this.salaryPerHourRepository = salaryPerHourRepository;
    }

    public List<StaffSalaryDto> getAllSalaryByAllStaffByDate(Integer storeId, LocalDate startDate, LocalDate endDate) {
        List<Staff> staffs = staffRepository.findAllByStoreId(storeId);
        SalaryPerHour salaryPerHour = salaryPerHourRepository.findByStoreId(storeId);
        List<StaffSalaryDto> staffSalaryDtos = new ArrayList<>();
        for (Staff staff : staffs) {
            List<Salary> salaries = salaryRepository.findAllSalaryByStoreIdAndStaffIdAndDate(storeId, staff.getId(), startDate, endDate);
            Double totalWorkHour = 0.0;
            LocalDate startWork = LocalDate.from(staff.getCreatedAt());
            Integer totalWorkShift = salaries.size();
            for (Salary salary : salaries) {
                totalWorkHour += salary.getWorkHour();

            }
            double termSalary = totalWorkHour * salaryPerHour.getHourlySalary();
            Integer termSalaryIn = (int) termSalary;
            StaffSalaryDto staffSalaryDto = StaffSalaryDto.builder()
                    .staffName(staff.getFullName())
                    .startWork(startWork)
                    .totalWorkShift(totalWorkShift)
                    .totalWorkTimeByMonth(totalWorkHour)
                    .termSalary(termSalaryIn)
                    .build();
            staffSalaryDtos.add(staffSalaryDto);
        }
        return staffSalaryDtos;
    }

    public Integer getSalaryPerHour(Integer storeId) {
        SalaryPerHour salaryPerHour = salaryPerHourRepository.findByStoreId(storeId);
        return salaryPerHour.getHourlySalary();
    }

    public void changeSalaryPerHour(Integer storeId, Integer salaryPerHour) {
        SalaryPerHour salaryPerHour1 = salaryPerHourRepository.findByStoreId(storeId);
        salaryPerHour1.setHourlySalary(salaryPerHour);
        salaryPerHourRepository.save(salaryPerHour1);
    }
}
