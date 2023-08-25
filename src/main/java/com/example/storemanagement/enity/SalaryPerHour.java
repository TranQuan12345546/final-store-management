package com.example.storemanagement.enity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="salary_per_hour")
public class SalaryPerHour {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "hourly_salary")
    private Integer hourlySalary;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
}
