package com.example.storemanagement.enity;

import com.example.storemanagement.constant.TypeOwner;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="owner")
public class Owner extends User{

    @Column(name = "created_at", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "status", nullable = false)
    private Boolean status;

    @Column(name = "type")
    private TypeOwner type;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Store> stores = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        status = false;
        createdAt = LocalDateTime.now();
    }
}
