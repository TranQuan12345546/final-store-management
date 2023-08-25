package com.example.storemanagement.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpsertStaffRequest {

    private Integer staffId;
    @NotEmpty
    private String username;
    private String fullName;
    @NotEmpty
    private String password;
    private String address;
    private String email;
    private String phone;
    private String birthday;
    private Integer store;



}
