package com.teamchallenge.easybuy.openapi.dto;

import lombok.Data;

@Data
public class UserAuthenticationRequest {
    private String email;
    private String password;
}
