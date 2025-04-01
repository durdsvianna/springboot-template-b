package com.example.customerdatasync.service;

import com.example.customerdatasync.dto.StateDto;
import com.example.customerdatasync.exception.ResourceNotFoundException;
import com.example.customerdatasync.exception.ServiceException;
import com.example.customerdatasync.model.State;
import com.example.customerdatasync.repository.StateRepository;
import com.example.customerdatasync.service.impl.StateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StateServiceImplTest {

    @Mock
    private StateRepository stateRepository;

    @InjectMocks
    private StateServiceImpl stateService;

    private State spState;
    private State rjState;
    private State mgState;

    @BeforeEach
    void setUp() {
        // Setup test data
        spState = new State("SP", "São Paulo");
        rjState = new State("RJ", "Rio de Janeiro");
        mgState = new State("MG", "Minas Gerais");
    }

    @Test
    @DisplayName("Should get all states successfully")
    void getAllStatesSuccessfully() {
        // Arrange
        List<State> states = Arrays.asList(spState, rjState, mgState);
        when(stateRepository.findAll()).thenReturn(states);

        // Act
        List<StateDto> result = stateService.getAllStates();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        
        // Verify each state is correctly mapped
        StateDto spDto = result.get(0);
        assertEquals("SP", spDto.stateCode());
        assertEquals("São Paulo", spDto.name());
        
        StateDto rjDto = result.get(1);
        assertEquals("RJ", rjDto.stateCode());
        assertEquals("Rio de Janeiro", rjDto.name());
        
        StateDto mgDto = result.get(2);
        assertEquals("MG", mgDto.stateCode());
        assertEquals("Minas Gerais", mgDto.name());
        
        verify(stateRepository).findAll();
    }
    
    @Test
    @DisplayName("Should throw ServiceException when getting all states fails due to database error")
    void getAllStatesWithDatabaseError() {
        // Arrange
        when(stateRepository.findAll()).thenThrow(mock(DataAccessException.class));
        
        // Act & Assert
        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> stateService.getAllStates()
        );
        
        assertTrue(exception.getMessage().contains("Failed to retrieve all states"));
        verify(stateRepository).findAll();
    }

    @Test
    @DisplayName("Should get state by code successfully")
    void getStateByCodeSuccessfully() {
        // Arrange
        when(stateRepository.findById("SP")).thenReturn(Optional.of(spState));

        // Act
        State result = stateService.getStateByCode("SP");

        // Assert
        assertNotNull(result);
        assertEquals("SP", result.getStateCode());
        assertEquals("São Paulo", result.getName());
        
        verify(stateRepository).findById("SP");
    }

    @Test
    @DisplayName("Should throw exception when state not found by code")
    void getStateByCodeNotFound() {
        // Arrange
        when(stateRepository.findById("XX")).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> stateService.getStateByCode("XX")
        );
        
        assertTrue(exception.getMessage().contains("State not found with stateCode"));
        verify(stateRepository).findById("XX");
    }
    
    @Test
    @DisplayName("Should throw ServiceException when getting state by code fails due to database error")
    void getStateByCodeWithDatabaseError() {
        // Arrange
        when(stateRepository.findById("SP")).thenThrow(mock(DataAccessException.class));
        
        // Act & Assert
        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> stateService.getStateByCode("SP")
        );
        
        assertTrue(exception.getMessage().contains("Failed to retrieve state with code: SP"));
        verify(stateRepository).findById("SP");
    }
    
    @Test
    @DisplayName("Should get state details successfully")
    void getStateDetailsSuccessfully() {
        // Arrange
        when(stateRepository.findById("SP")).thenReturn(Optional.of(spState));
        
        // Act
        StateDto result = stateService.getStateDetails("SP");
        
        // Assert
        assertNotNull(result);
        assertEquals("SP", result.stateCode());
        assertEquals("São Paulo", result.name());
        
        verify(stateRepository).findById("SP");
    }
    
    @Test
    @DisplayName("Should throw ResourceNotFoundException when state details not found")
    void getStateDetailsNotFound() {
        // Arrange
        when(stateRepository.findById("XX")).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> stateService.getStateDetails("XX")
        );
        
        assertTrue(exception.getMessage().contains("State not found with stateCode"));
        verify(stateRepository).findById("XX");
    }

    @Test
    @DisplayName("Should return empty list when no states exist")
    void getAllStatesEmpty() {
        // Arrange
        when(stateRepository.findAll()).thenReturn(List.of());

        // Act
        List<StateDto> result = stateService.getAllStates();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(stateRepository).findAll();
    }
} 