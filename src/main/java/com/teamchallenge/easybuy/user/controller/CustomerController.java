package com.teamchallenge.easybuy.user.controller;

import com.teamchallenge.easybuy.user.dto.AddressDto;
import com.teamchallenge.easybuy.user.dto.CustomerProfileDto;
import com.teamchallenge.easybuy.user.dto.CustomerProfileResponseDto;
import com.teamchallenge.easybuy.user.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Exposes API endpoints in CustomerController.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
public class CustomerController {
    private final CustomerService customerService;

    @Operation(summary = "Getting user data", description = "Obtaining information about the user and their address")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Data received successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerProfileResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @GetMapping("/profile")
    public ResponseEntity<?> getCustomerProfile() {
        return ResponseEntity.ok(customerService.getCustomerProfile());
    }

    @Operation(summary = "Changing user information", description = "Obtaining basic information about the user and changing it")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Data successfully modified",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerProfileDto.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @PutMapping("/profile")
    public ResponseEntity<?> updateCustomerProfile(@Valid @RequestBody CustomerProfileDto customer, HttpServletRequest request) {
        System.out.println("1");
        return ResponseEntity.ok(customerService.updateCustomerProfile(customer, request));
    }

    @Operation(summary = "Changing user address information", description = "Obtaining updated user addresses")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Data successfully modified",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AddressDto.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @PutMapping("/address")
    public ResponseEntity<?> updateCustomerAddress(@Valid @RequestBody AddressDto addressDto) {
        return ResponseEntity.ok(customerService.updateCustomerAddress(addressDto));
    }

    @Operation(summary = "Deleting a user", description = "Deleting a user and all their data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User successfully deleted"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @DeleteMapping()
    public ResponseEntity<?> deleteCustomerProfile() {
        try {
            customerService.deleteCustomer();
            return ResponseEntity.noContent().build();
        } catch (IOException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
