package com.example.customerdatasync.service.impl;

import com.example.customerdatasync.dto.AddressRequestDto;
import com.example.customerdatasync.dto.AddressResponseDto;
import com.example.customerdatasync.dto.StateDto;
import com.example.customerdatasync.exception.ResourceNotFoundException;
import com.example.customerdatasync.model.Address;
import com.example.customerdatasync.model.State;
import com.example.customerdatasync.repository.AddressRepository;
import com.example.customerdatasync.service.AddressService;
import com.example.customerdatasync.service.StateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final StateService stateService;

    @Override
    @Transactional
    public AddressResponseDto createAddress(AddressRequestDto addressRequestDto) {
        State state = stateService.getStateByCode(addressRequestDto.stateCode());
        
        Address address = Address.builder()
                .street(addressRequestDto.street())
                .complement(addressRequestDto.complement())
                .zipCode(normalizeZipCode(addressRequestDto.zipCode()))
                .city(addressRequestDto.city())
                .state(state)
                .build();
        
        Address savedAddress = addressRepository.save(address);
        return mapToResponseDto(savedAddress);
    }

    @Override
    public AddressResponseDto getAddressById(Long id) {
        Address address = findAddressById(id);
        return mapToResponseDto(address);
    }

    @Override
    @Transactional
    public AddressResponseDto updateAddress(Long id, AddressRequestDto addressRequestDto) {
        Address existingAddress = findAddressById(id);
        State state = stateService.getStateByCode(addressRequestDto.stateCode());
        
        existingAddress.setStreet(addressRequestDto.street());
        existingAddress.setComplement(addressRequestDto.complement());
        existingAddress.setZipCode(normalizeZipCode(addressRequestDto.zipCode()));
        existingAddress.setCity(addressRequestDto.city());
        existingAddress.setState(state);
        
        Address updatedAddress = addressRepository.save(existingAddress);
        return mapToResponseDto(updatedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        Address address = findAddressById(id);
        addressRepository.delete(address);
    }

    @Override
    public List<AddressResponseDto> getAddressesByState(String stateCode) {
        State state = stateService.getStateByCode(stateCode);
        return addressRepository.findByState(state)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    private Address findAddressById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));
    }
    
    private AddressResponseDto mapToResponseDto(Address address) {
        StateDto stateDto = new StateDto(
                address.getState().getStateCode(),
                address.getState().getName()
        );
        
        return new AddressResponseDto(
                address.getId(),
                address.getStreet(),
                address.getComplement(),
                address.getZipCode(),
                address.getCity(),
                stateDto
        );
    }
    
    private String normalizeZipCode(String zipCode) {
        // Ensure zipCode is in format 99999-999
        if (zipCode.length() == 8 && !zipCode.contains("-")) {
            return zipCode.substring(0, 5) + "-" + zipCode.substring(5);
        }
        return zipCode;
    }
} 