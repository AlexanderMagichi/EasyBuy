package com.teamchallenge.easybuy.openapi.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class UserDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String phoneNumber;
    private String email;
    private AddressDto address;
    private String avatarLink;
}
