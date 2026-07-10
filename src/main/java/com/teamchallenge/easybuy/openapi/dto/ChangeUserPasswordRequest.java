package com.teamchallenge.easybuy.openapi.dto;

import lombok.Data;

@Data
public class ChangeUserPasswordRequest {
    private String oldPassword;
    private String newPassword;
}
