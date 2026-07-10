package com.teamchallenge.easybuy.openapi.dto;

import lombok.Data;

@Data
public class DeliveryAddressRequest {
    private String label;
    private String line;
    private String city;
    private String country;
    private String postcode;
}
