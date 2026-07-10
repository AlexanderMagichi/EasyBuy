package com.teamchallenge.easybuy.openapi.dto;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String email;
    private String code;
    private String password;
}
