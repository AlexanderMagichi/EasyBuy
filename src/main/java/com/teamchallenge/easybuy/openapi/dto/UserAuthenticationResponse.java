package com.teamchallenge.easybuy.openapi.dto;

import lombok.Data;

@Data
public class UserAuthenticationResponse {
    private String token;
    private String refreshToken;
}
