package com.example.storemanagement.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpsertGroupProductRequest {
    private Integer id;
    @NotEmpty(message = "Tên không được để trống")
    @NotNull
    private String name;
    @NotNull
    private Integer storeId;
}
