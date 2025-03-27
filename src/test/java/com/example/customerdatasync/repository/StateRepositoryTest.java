package com.example.customerdatasync.repository;

import com.example.customerdatasync.model.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class StateRepositoryTest {

    @Autowired
    private StateRepository stateRepository;

    @BeforeEach
    void setUp() {
        // Clean up repository before each test
        stateRepository.deleteAll();
    }

    @Test
    @DisplayName("Should save state successfully")
    void saveStateSuccessfully() {
        // Arrange
        State state = new State("SP", "São Paulo");
        
        // Act
        State savedState = stateRepository.save(state);
        
        // Assert
        assertNotNull(savedState);
        assertEquals("SP", savedState.getStateCode());
        assertEquals("São Paulo", savedState.getName());
    }

    @Test
    @DisplayName("Should find state by ID (state code) successfully")
    void findByIdSuccessfully() {
        // Arrange
        State state = new State("SP", "São Paulo");
        stateRepository.save(state);
        
        // Act
        Optional<State> foundState = stateRepository.findById("SP");
        
        // Assert
        assertTrue(foundState.isPresent());
        assertEquals("SP", foundState.get().getStateCode());
        assertEquals("São Paulo", foundState.get().getName());
    }

    @Test
    @DisplayName("Should not find state when ID doesn't exist")
    void findByIdNotFound() {
        // Act
        Optional<State> foundState = stateRepository.findById("XX");
        
        // Assert
        assertFalse(foundState.isPresent());
    }

    @Test
    @DisplayName("Should find all states successfully")
    void findAllStatesSuccessfully() {
        // Arrange
        State sp = new State("SP", "São Paulo");
        State rj = new State("RJ", "Rio de Janeiro");
        State mg = new State("MG", "Minas Gerais");
        
        stateRepository.save(sp);
        stateRepository.save(rj);
        stateRepository.save(mg);
        
        // Act
        List<State> states = stateRepository.findAll();
        
        // Assert
        assertNotNull(states);
        assertEquals(3, states.size());
        
        // Verify that all states are included
        assertTrue(states.stream().anyMatch(s -> "SP".equals(s.getStateCode())));
        assertTrue(states.stream().anyMatch(s -> "RJ".equals(s.getStateCode())));
        assertTrue(states.stream().anyMatch(s -> "MG".equals(s.getStateCode())));
    }

    @Test
    @DisplayName("Should return empty list when no states exist")
    void findAllStatesEmpty() {
        // Act
        List<State> states = stateRepository.findAll();
        
        // Assert
        assertNotNull(states);
        assertTrue(states.isEmpty());
    }

    @Test
    @DisplayName("Should delete state successfully")
    void deleteStateSuccessfully() {
        // Arrange
        State state = new State("SP", "São Paulo");
        stateRepository.save(state);
        
        // Act
        stateRepository.delete(state);
        Optional<State> foundState = stateRepository.findById("SP");
        
        // Assert
        assertFalse(foundState.isPresent());
    }

    @Test
    @DisplayName("Should update state successfully")
    void updateStateSuccessfully() {
        // Arrange
        State state = new State("SP", "São Paulo");
        stateRepository.save(state);
        
        // Act
        State stateToUpdate = stateRepository.findById("SP").get();
        stateToUpdate.setName("São Paulo - Updated");
        stateRepository.save(stateToUpdate);
        
        // Assert
        State updatedState = stateRepository.findById("SP").get();
        assertEquals("São Paulo - Updated", updatedState.getName());
    }
} 