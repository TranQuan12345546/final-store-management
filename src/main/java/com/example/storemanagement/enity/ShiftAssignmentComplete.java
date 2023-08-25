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
@Table(name="shift_assignment_complete")
public class ShiftAssignmentComplete {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "work_day", columnDefinition = "TIMESTAMP")
    private LocalDate workDay;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "start_shift", columnDefinition = "TIME")
    private LocalTime startShift;

    @Column(name = "end_shift", columnDefinition = "TIME")
    private LocalTime endShift;

    @Column(name = "check_in_time", columnDefinition = "TIME")
    private LocalTime checkInTime;

    @Column(name = "check_out_time", columnDefinition = "TIME")
    private LocalTime checkOutTime;

    @ManyToMany
    @JoinTable(name = "shift_assignment_complete_staff",
            joinColumns = @JoinColumn(name = "shift_assignment_complete_id"),
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
