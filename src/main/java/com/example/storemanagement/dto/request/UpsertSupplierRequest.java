package com.example.storemanagement.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpsertSupplierRequest {
    @NotEmpty
    private String name;
    private String address;
    private String email;
    private String phone;
}
