package com.example.storemanagement.dto.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterOwner {
    private String fullName;
    private String username;
    private String password;
    private String nameStore;
    private String phone;
    private String email;
    private String address;

}
