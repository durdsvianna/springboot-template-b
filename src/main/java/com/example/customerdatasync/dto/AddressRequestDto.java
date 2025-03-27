package com.example.customerdatasync.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddressRequestDto(
    @NotBlank(message = "Street is required")
    String street,
    
    String complement,
    
    @NotBlank(message = "Zip code is required")
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "Zip code must be in format 99999-999 or 99999999")
    String zipCode,
    
    @NotBlank(message = "City is required")
    String city,
    
    @NotBlank(message = "State code is required")
    String stateCode
) {} 