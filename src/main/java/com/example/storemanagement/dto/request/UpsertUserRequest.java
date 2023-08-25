package com.example.storemanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record UpsertUserRequest(@NotEmpty(message = "username không được để trống")
                                String username,
                                String fullName,
                                @NotEmpty(message = "Password không được để trống")
                                String password,
                                @NotEmpty(message = "Email không được để trống")
                                @Email
                                String email,
                                String phone,
                                String address,
                                Integer ownerId) {
}
