package com.teamchallenge.easybuy.openapi.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class DeliveryAddressDto {
    private UUID id;
    private String label;
    private String line;
    private String city;
    private String country;
    private String postcode;
    private Boolean isDefault;
}
