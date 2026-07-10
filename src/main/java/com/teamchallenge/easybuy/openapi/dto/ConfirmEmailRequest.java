package com.teamchallenge.easybuy.openapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmEmailRequest {
    private String token;

    public String getCode() {
        return token;
    }
}
