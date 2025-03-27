package com.example.customerdatasync.controller;

import com.example.customerdatasync.dto.AddressRequestDto;
import com.example.customerdatasync.dto.AddressResponseDto;
import com.example.customerdatasync.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
@Tag(name = "Address", description = "API for Address management")
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    @Operation(summary = "Create new address",
            description = "Endpoint to create a new address")
    @ApiResponse(responseCode = "201", description = "Address created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    public ResponseEntity<AddressResponseDto> createAddress(@Valid @RequestBody AddressRequestDto addressRequestDto) {
        AddressResponseDto addressResponseDto = addressService.createAddress(addressRequestDto);
        return new ResponseEntity<>(addressResponseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get address by ID",
            description = "Endpoint to get an address by its ID")
    @ApiResponse(responseCode = "200", description = "Address found")
    @ApiResponse(responseCode = "404", description = "Address not found")
    public ResponseEntity<AddressResponseDto> getAddressById(@PathVariable Long id) {
        AddressResponseDto addressResponseDto = addressService.getAddressById(id);
        return ResponseEntity.ok(addressResponseDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update address",
            description = "Endpoint to update an existing address")
    @ApiResponse(responseCode = "200", description = "Address updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "404", description = "Address not found")
    public ResponseEntity<AddressResponseDto> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequestDto addressRequestDto) {
        AddressResponseDto addressResponseDto = addressService.updateAddress(id, addressRequestDto);
        return ResponseEntity.ok(addressResponseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete address",
            description = "Endpoint to delete an existing address")
    @ApiResponse(responseCode = "204", description = "Address deleted successfully")
    @ApiResponse(responseCode = "404", description = "Address not found")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/state/{stateCode}")
    @Operation(summary = "Get addresses by State",
            description = "Endpoint to get addresses by State code")
    @ApiResponse(responseCode = "200", description = "List of addresses for the specified State")
    @ApiResponse(responseCode = "404", description = "State not found")
    public ResponseEntity<List<AddressResponseDto>> getAddressesByState(@PathVariable String stateCode) {
        List<AddressResponseDto> addressResponseDtos = addressService.getAddressesByState(stateCode);
        return ResponseEntity.ok(addressResponseDtos);
    }
} 