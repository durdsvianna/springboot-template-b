package com.example.customerdatasync.service.impl;

import com.example.customerdatasync.dto.AddressRequestDto;
import com.example.customerdatasync.dto.AddressResponseDto;
import com.example.customerdatasync.dto.StateDto;
import com.example.customerdatasync.exception.ResourceNotFoundException;
import com.example.customerdatasync.exception.ServiceException;
import com.example.customerdatasync.model.Address;
import com.example.customerdatasync.model.State;
import com.example.customerdatasync.repository.AddressRepository;
import com.example.customerdatasync.service.AddressService;
import com.example.customerdatasync.service.StateService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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
        try {
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
        } catch (ResourceNotFoundException e) {
            // Re-throw resource not found exceptions as is
            throw e;
        } catch (DataAccessException e) {
            // Wrap database exceptions
            throw new ServiceException("Failed to create address", "createAddress", e);
        } catch (Exception e) {
            // Catch all other exceptions
            throw new ServiceException("Unexpected error creating address", e);
        }
    }

    @Override
    public AddressResponseDto getAddressById(Long id) {
        try {
            Address address = findAddressById(id);
            return mapToResponseDto(address);
        } catch (ResourceNotFoundException e) {
            // Re-throw resource not found exceptions as is
            throw e;
        } catch (Exception e) {
            // Catch all other exceptions
            throw new ServiceException("Error retrieving address with ID: " + id, e);
        }
    }

    @Override
    @Transactional
    public AddressResponseDto updateAddress(Long id, AddressRequestDto addressRequestDto) {
        try {
            Address existingAddress = findAddressById(id);
            State state = stateService.getStateByCode(addressRequestDto.stateCode());
            
            existingAddress.setStreet(addressRequestDto.street());
            existingAddress.setComplement(addressRequestDto.complement());
            existingAddress.setZipCode(normalizeZipCode(addressRequestDto.zipCode()));
            existingAddress.setCity(addressRequestDto.city());
            existingAddress.setState(state);
            
            Address updatedAddress = addressRepository.save(existingAddress);
            return mapToResponseDto(updatedAddress);
        } catch (ResourceNotFoundException e) {
            // Re-throw resource not found exceptions as is
            throw e;
        } catch (DataAccessException e) {
            // Wrap database exceptions
            throw new ServiceException("Failed to update address with ID: " + id, "updateAddress", e);
        } catch (Exception e) {
            // Catch all other exceptions
            throw new ServiceException("Unexpected error updating address with ID: " + id, e);
        }
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        try {
            Address address = findAddressById(id);
            addressRepository.delete(address);
        } catch (ResourceNotFoundException e) {
            // Re-throw resource not found exceptions as is
            throw e;
        } catch (DataAccessException e) {
            // Wrap database exceptions
            throw new ServiceException("Failed to delete address with ID: " + id, "deleteAddress", e);
        } catch (Exception e) {
            // Catch all other exceptions
            throw new ServiceException("Unexpected error deleting address with ID: " + id, e);
        }
    }

    @Override
    public List<AddressResponseDto> getAddressesByState(String stateCode) {
        try {
            State state = stateService.getStateByCode(stateCode);
            return addressRepository.findByState(state)
                    .stream()
                    .map(this::mapToResponseDto)
                    .collect(Collectors.toList());
        } catch (ResourceNotFoundException e) {
            // Re-throw resource not found exceptions as is
            throw e;
        } catch (DataAccessException e) {
            // Wrap database exceptions
            throw new ServiceException("Failed to retrieve addresses for state: " + stateCode, "getAddressesByState", e);
        } catch (Exception e) {
            // Catch all other exceptions and return empty list
            throw new ServiceException("Unexpected error retrieving addresses for state: " + stateCode, e);
        }
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