package com.example.storemanagement.enity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="staff")
public class Staff extends User{

    @Column(name = "birth_day")
    private String birthday;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "start_work")
    private LocalDate startWork;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToMany(mappedBy = "staffs")
    private List<ShiftAssignment> shiftAssignments = new ArrayList<>();

    @ManyToMany(mappedBy = "staffs")
    private List<ShiftAssignmentComplete> shiftAssignmentCompletes = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        status = true;
        createdAt = LocalDateTime.now();
    }
}
