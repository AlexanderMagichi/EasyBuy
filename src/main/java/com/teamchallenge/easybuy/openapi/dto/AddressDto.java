package com.teamchallenge.easybuy.openapi.dto;

import lombok.Data;

@Data
public class AddressDto {
    private String country;
    private String city;
    private String line;
    private String postcode;
}
