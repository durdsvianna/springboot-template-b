package com.example.customerdatasync.dto;

public record AddressResponseDto(
    Long id,
    String street,
    String complement,
    String zipCode,
    String city,
    StateDto state
) {} 