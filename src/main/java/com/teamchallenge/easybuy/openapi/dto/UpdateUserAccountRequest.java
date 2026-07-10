package com.teamchallenge.easybuy.openapi.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserAccountRequest {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String phoneNumber;
    private AddressDto address;
}
