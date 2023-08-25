package com.example.storemanagement.enity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="shift_assignment")
public class ShiftAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "work_day", columnDefinition = "TIMESTAMP")
    private LocalDate workDay;

    @Column(name = "work_day_index")
    private Integer workDayIndex;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "work_shift_id")
    private WorkShift workShift;

    @Column(name = "work_shift_index")
    private Integer workShiftIndex;

    @Column(name = "check-in-time")
    private LocalTime checkInTime;

    @Column(name = "check-out-time")
    private LocalTime checkOutTime;

    @ManyToMany
    @JoinTable(name = "shift_assignment_staff",
            joinColumns = @JoinColumn(name = "shift_assignment_id"),
            inverseJoinColumns = @JoinColumn(name = "staff_id"))
    private List<Staff> staffs = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
