package com.example.storemanagement.dto;

import com.example.storemanagement.enity.Owner;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OwnerDto {
    private Integer id;
    private String username;
    private String email;
    private String phone;
    private LocalDateTime createdAt;

    public static OwnerDto toOwnerDto(Owner owner) {
        OwnerDto tmp = new OwnerDto();
        tmp.setId(owner.getId());
        tmp.setUsername(owner.getUsername());
        tmp.setEmail(owner.getEmail());
        tmp.setPhone(owner.getPhone());
        tmp.setCreatedAt(owner.getCreatedAt());
        return tmp;
    }
}