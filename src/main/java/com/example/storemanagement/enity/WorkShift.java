package com.example.storemanagement.enity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="work_shift")
public class WorkShift {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", columnDefinition = "VARCHAR(255)")
    private String name;

    @Column(name = "start_shift", columnDefinition = "TIME")
    private LocalTime startShift;

    @Column(name = "end_shift", columnDefinition = "TIME")
    private LocalTime endShift;
    @Column(name = "order_shift")
    private Integer orderShift;

    @OneToMany(mappedBy = "workShift")
    private List<ShiftAssignment> shiftAssignments = new ArrayList<>();

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
