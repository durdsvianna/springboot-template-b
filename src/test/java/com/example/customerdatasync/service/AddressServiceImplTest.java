package com.example.customerdatasync.service;

import com.example.customerdatasync.dto.AddressRequestDto;
import com.example.customerdatasync.dto.AddressResponseDto;
import com.example.customerdatasync.dto.StateDto;
import com.example.customerdatasync.exception.ResourceNotFoundException;
import com.example.customerdatasync.model.Address;
import com.example.customerdatasync.model.State;
import com.example.customerdatasync.repository.AddressRepository;
import com.example.customerdatasync.service.impl.AddressServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private StateService stateService;

    @InjectMocks
    private AddressServiceImpl addressService;

    private State state;
    private Address address;
    private AddressRequestDto addressRequestDto;

    @BeforeEach
    void setUp() {
        // Initialize test data
        state = new State("SP", "São Paulo");
        
        address = Address.builder()
                .id(1L)
                .street("Av Paulista, 1000")
                .complement("Apto 123")
                .zipCode("01310-100")
                .city("São Paulo")
                .state(state)
                .build();
        
        addressRequestDto = new AddressRequestDto(
                "Av Paulista, 1000",
                "Apto 123",
                "01310100",
                "São Paulo",
                "SP"
        );
    }

    @Test
    @DisplayName("Should create address successfully")
    void createAddressSuccessfully() {
        // Arrange
        when(stateService.getStateByCode(anyString())).thenReturn(state);
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        
        // Act
        AddressResponseDto result = addressService.createAddress(addressRequestDto);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Av Paulista, 1000", result.street());
        assertEquals("Apto 123", result.complement());
        assertEquals("01310-100", result.zipCode());
        assertEquals("São Paulo", result.city());
        assertEquals("SP", result.state().stateCode());
        assertEquals("São Paulo", result.state().name());
        
        verify(stateService).getStateByCode("SP");
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    @DisplayName("Should get address by ID successfully")
    void getAddressByIdSuccessfully() {
        // Arrange
        when(addressRepository.findById(anyLong())).thenReturn(Optional.of(address));
        
        // Act
        AddressResponseDto result = addressService.getAddressById(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Av Paulista, 1000", result.street());
        assertEquals("São Paulo", result.city());
        
        verify(addressRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when address not found by ID")
    void getAddressByIdNotFound() {
        // Arrange
        when(addressRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> addressService.getAddressById(999L)
        );
        
        assertEquals("Address not found with id : '999'", exception.getMessage());
        verify(addressRepository).findById(999L);
    }

    @Test
    @DisplayName("Should update address successfully")
    void updateAddressSuccessfully() {
        // Arrange
        AddressRequestDto updateRequest = new AddressRequestDto(
                "Av Paulista, 2000",
                "Apto 456",
                "01310200",
                "São Paulo",
                "SP"
        );
        
        Address updatedAddress = Address.builder()
                .id(1L)
                .street("Av Paulista, 2000")
                .complement("Apto 456")
                .zipCode("01310-200")
                .city("São Paulo")
                .state(state)
                .build();
        
        when(addressRepository.findById(anyLong())).thenReturn(Optional.of(address));
        when(stateService.getStateByCode(anyString())).thenReturn(state);
        when(addressRepository.save(any(Address.class))).thenReturn(updatedAddress);
        
        // Act
        AddressResponseDto result = addressService.updateAddress(1L, updateRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Av Paulista, 2000", result.street());
        assertEquals("Apto 456", result.complement());
        assertEquals("01310-200", result.zipCode());
        
        verify(addressRepository).findById(1L);
        verify(stateService).getStateByCode("SP");
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    @DisplayName("Should delete address successfully")
    void deleteAddressSuccessfully() {
        // Arrange
        when(addressRepository.findById(anyLong())).thenReturn(Optional.of(address));
        doNothing().when(addressRepository).delete(any(Address.class));
        
        // Act
        addressService.deleteAddress(1L);
        
        // Assert
        verify(addressRepository).findById(1L);
        verify(addressRepository).delete(address);
    }

    @Test
    @DisplayName("Should throw exception when trying to delete non-existent address")
    void deleteAddressNotFound() {
        // Arrange
        when(addressRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> addressService.deleteAddress(999L)
        );
        
        assertEquals("Address not found with id : '999'", exception.getMessage());
        verify(addressRepository).findById(999L);
        verify(addressRepository, never()).delete(any(Address.class));
    }

    @Test
    @DisplayName("Should get addresses by state successfully")
    void getAddressesByStateSuccessfully() {
        // Arrange
        Address address2 = Address.builder()
                .id(2L)
                .street("Rua Augusta, 500")
                .complement("Sala 45")
                .zipCode("01304-000")
                .city("São Paulo")
                .state(state)
                .build();
        
        List<Address> addresses = Arrays.asList(address, address2);
        
        when(stateService.getStateByCode(anyString())).thenReturn(state);
        when(addressRepository.findByState(any(State.class))).thenReturn(addresses);
        
        // Act
        List<AddressResponseDto> result = addressService.getAddressesByState("SP");
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals(2L, result.get(1).id());
        assertEquals("SP", result.get(0).state().stateCode());
        assertEquals("SP", result.get(1).state().stateCode());
        
        verify(stateService).getStateByCode("SP");
        verify(addressRepository).findByState(state);
    }

    @Test
    @DisplayName("Should return empty list when no addresses found for state")
    void getAddressesByStateEmpty() {
        // Arrange
        when(stateService.getStateByCode(anyString())).thenReturn(state);
        when(addressRepository.findByState(any(State.class))).thenReturn(Collections.emptyList());
        
        // Act
        List<AddressResponseDto> result = addressService.getAddressesByState("SP");
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(stateService).getStateByCode("SP");
        verify(addressRepository).findByState(state);
    }

    @Test
    @DisplayName("Should normalize zip code format correctly")
    void shouldNormalizeZipCodeFormat() {
        // Arrange
        AddressRequestDto requestWithUnformattedZip = new AddressRequestDto(
                "Av Paulista, 1000",
                "Apto 123",
                "01310100", // Without hyphen
                "São Paulo",
                "SP"
        );
        
        when(stateService.getStateByCode(anyString())).thenReturn(state);
        
        Address addressWithFormattedZip = Address.builder()
                .id(1L)
                .street("Av Paulista, 1000")
                .complement("Apto 123")
                .zipCode("01310-100") // With hyphen
                .city("São Paulo")
                .state(state)
                .build();
        
        when(addressRepository.save(any(Address.class))).thenReturn(addressWithFormattedZip);
        
        // Act
        AddressResponseDto result = addressService.createAddress(requestWithUnformattedZip);
        
        // Assert
        assertNotNull(result);
        assertEquals("01310-100", result.zipCode());
        
        verify(addressRepository).save(argThat(address -> 
                "01310-100".equals(address.getZipCode())
        ));
    }
}