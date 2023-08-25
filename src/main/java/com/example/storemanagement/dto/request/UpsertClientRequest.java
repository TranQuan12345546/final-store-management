package com.example.storemanagement.dto.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpsertClientRequest {
    private Integer clientId;
    private String fullName;
    private String address;
    private String email;
    private String phone;
    private String birthday;
    private Integer store;

}
