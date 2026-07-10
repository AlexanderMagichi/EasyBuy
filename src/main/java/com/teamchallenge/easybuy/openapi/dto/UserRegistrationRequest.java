package com.teamchallenge.easybuy.openapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
