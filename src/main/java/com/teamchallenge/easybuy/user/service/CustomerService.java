package com.teamchallenge.easybuy.user.service;

import com.teamchallenge.easybuy.user.dto.AddressDto;
import com.teamchallenge.easybuy.user.dto.CustomerProfileDto;
import com.teamchallenge.easybuy.user.dto.CustomerProfileResponseDto;
import com.teamchallenge.easybuy.user.mapper.AddressMapper;
import com.teamchallenge.easybuy.user.mapper.CustomerMapper;
import com.teamchallenge.easybuy.auth.service.EmailConfirmationService;
import com.teamchallenge.easybuy.auth.service.PasswordResetService;
import com.teamchallenge.easybuy.auth.service.PhoneValidationService;
import com.teamchallenge.easybuy.auth.service.TokenService;
import com.teamchallenge.easybuy.user.entity.Address;
import com.teamchallenge.easybuy.user.entity.Customer;
import com.teamchallenge.easybuy.user.repository.CustomerRepository;
import com.teamchallenge.easybuy.infrastructure.image.CloudinaryImageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

/**
 * Provides business operations for CustomerService.
 */
@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AddressMapper addressMapper;
    private final EmailConfirmationService emailConfirmationService;
    private final TokenService tokenService;
    private final PasswordResetService passwordResetService;
    private final CloudinaryImageService cloudinaryImageService;
    private final PhoneValidationService phoneValidationService;

    public CustomerProfileResponseDto getCustomerProfile() {
        Customer customer = getCustomer();
        return new CustomerProfileResponseDto(
                customerMapper.toDto(customer),
                addressMapper.toDto(customer.getAddress()));
    }

    public CustomerProfileDto updateCustomerProfile(CustomerProfileDto customerProfile, HttpServletRequest request) {
        Customer customer = getCustomer();
        customer.setName(customerProfile.getName());
        customer.setBirthday(customerProfile.getBirthday());
        customer.setPhoneNumber(phoneValidationService.formatToE164(customerProfile.getPhoneNumber()));
        if (!customer.getEmail().equals(customerProfile.getEmail())) {
            customer.setEmail(customerProfile.getEmail());
            customer.setEmailVerified(false);
            emailConfirmationService.sendConfirmationEmail(customer);
        }

        return customerMapper.toDto(customerRepository.save(customer));
    }

    public AddressDto updateCustomerAddress(AddressDto addressDto) {
        Customer customer = getCustomer();
        Address address = customer.getAddress();
        if (address == null) {
            address = new Address();
            customer.setAddress(address);
        }
        address.setCity(addressDto.getCity());
        address.setCountry(addressDto.getCountry());
        address.setStreet(addressDto.getStreet());
        Customer newCustomer = customerRepository.save(customer);
        return addressMapper.toDto(newCustomer.getAddress());
    }

    @Transactional
    public void deleteCustomer() throws IOException {
        Customer customer = getCustomer();
        String avatarUrl = customer.getAvatarUrl();
        String publicId = cloudinaryImageService.extractPublicIdFromUrl(avatarUrl);
        if (publicId != null) {
            cloudinaryImageService.deleteImage(publicId);
        }
        tokenService.deleteAllTokensForUser(customer);
        emailConfirmationService.deleteAllByUser(customer);
        passwordResetService.deleteAllByUser(customer);
        customerRepository.delete(customer);
    }

    private Customer getCustomer() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return customerRepository.findByEmail(username)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "customer not found: " + username));
    }

}
