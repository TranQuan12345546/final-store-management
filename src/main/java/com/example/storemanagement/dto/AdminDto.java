package com.example.storemanagement.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AdminDto {
    private Integer id;
    private String username;
    private String email;
}
