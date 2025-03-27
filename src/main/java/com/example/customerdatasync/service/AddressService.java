package com.example.customerdatasync.service;

import com.example.customerdatasync.dto.AddressRequestDto;
import com.example.customerdatasync.dto.AddressResponseDto;

import java.util.List;

public interface AddressService {
    AddressResponseDto createAddress(AddressRequestDto addressRequestDto);
    AddressResponseDto getAddressById(Long id);
    AddressResponseDto updateAddress(Long id, AddressRequestDto addressRequestDto);
    void deleteAddress(Long id);
    List<AddressResponseDto> getAddressesByState(String stateCode);
} 