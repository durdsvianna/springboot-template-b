package com.example.customerdatasync.repository;

import com.example.customerdatasync.model.Address;
import com.example.customerdatasync.model.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private StateRepository stateRepository;

    private State spState;
    private State rjState;
    private Address address1;
    private Address address2;
    private Address address3;

    @BeforeEach
    void setUp() {
        // Clean up repositories
        addressRepository.deleteAll();
        stateRepository.deleteAll();

        // Create test states
        spState = new State("SP", "São Paulo");
        rjState = new State("RJ", "Rio de Janeiro");
        stateRepository.save(spState);
        stateRepository.save(rjState);

        // Create test addresses
        address1 = Address.builder()
                .street("Av Paulista, 1000")
                .complement("Apto 123")
                .zipCode("01310-100")
                .city("São Paulo")
                .state(spState)
                .build();

        address2 = Address.builder()
                .street("Rua Augusta, 500")
                .complement("Sala 45")
                .zipCode("01304-000")
                .city("São Paulo")
                .state(spState)
                .build();

        address3 = Address.builder()
                .street("Av Atlântica, 1500")
                .complement("Bloco B")
                .zipCode("22021-001")
                .city("Rio de Janeiro")
                .state(rjState)
                .build();

        // Save test addresses
        address1 = addressRepository.save(address1);
        address2 = addressRepository.save(address2);
        address3 = addressRepository.save(address3);
    }

    @Test
    @DisplayName("Should save address successfully")
    void saveAddressSuccessfully() {
        // Arrange
        Address newAddress = Address.builder()
                .street("Rua Teste, 100")
                .complement("Casa")
                .zipCode("05432-020")
                .city("São Paulo")
                .state(spState)
                .build();

        // Act
        Address savedAddress = addressRepository.save(newAddress);

        // Assert
        assertNotNull(savedAddress);
        assertNotNull(savedAddress.getId());
        assertEquals("Rua Teste, 100", savedAddress.getStreet());
        assertEquals("Casa", savedAddress.getComplement());
        assertEquals("05432-020", savedAddress.getZipCode());
        assertEquals("São Paulo", savedAddress.getCity());
        assertEquals(spState.getStateCode(), savedAddress.getState().getStateCode());
    }

    @Test
    @DisplayName("Should find address by ID successfully")
    void findByIdSuccessfully() {
        // Act
        Optional<Address> foundAddress = addressRepository.findById(address1.getId());

        // Assert
        assertTrue(foundAddress.isPresent());
        assertEquals(address1.getId(), foundAddress.get().getId());
        assertEquals(address1.getStreet(), foundAddress.get().getStreet());
        assertEquals(address1.getCity(), foundAddress.get().getCity());
    }

    @Test
    @DisplayName("Should not find address when ID doesn't exist")
    void findByIdNotFound() {
        // Act
        Optional<Address> foundAddress = addressRepository.findById(999L);

        // Assert
        assertFalse(foundAddress.isPresent());
    }

    @Test
    @DisplayName("Should find addresses by state successfully")
    void findByStateSuccessfully() {
        // Act
        List<Address> spAddresses = addressRepository.findByState(spState);
        List<Address> rjAddresses = addressRepository.findByState(rjState);

        // Assert
        assertNotNull(spAddresses);
        assertEquals(2, spAddresses.size());
        assertTrue(spAddresses.stream().allMatch(a -> "SP".equals(a.getState().getStateCode())));

        assertNotNull(rjAddresses);
        assertEquals(1, rjAddresses.size());
        assertTrue(rjAddresses.stream().allMatch(a -> "RJ".equals(a.getState().getStateCode())));
    }

    @Test
    @DisplayName("Should return empty list when no addresses exist for a state")
    void findByStateEmpty() {
        // Arrange
        State mgState = new State("MG", "Minas Gerais");
        stateRepository.save(mgState);

        // Act
        List<Address> mgAddresses = addressRepository.findByState(mgState);

        // Assert
        assertNotNull(mgAddresses);
        assertTrue(mgAddresses.isEmpty());
    }

    @Test
    @DisplayName("Should update address successfully")
    void updateAddressSuccessfully() {
        // Arrange
        Address addressToUpdate = addressRepository.findById(address1.getId()).get();
        addressToUpdate.setStreet("Av Paulista, 2000");
        addressToUpdate.setComplement("Apto 456");
        addressToUpdate.setState(rjState);

        // Act
        Address updatedAddress = addressRepository.save(addressToUpdate);
        Optional<Address> foundAddress = addressRepository.findById(address1.getId());

        // Assert
        assertTrue(foundAddress.isPresent());
        assertEquals("Av Paulista, 2000", foundAddress.get().getStreet());
        assertEquals("Apto 456", foundAddress.get().getComplement());
        assertEquals("RJ", foundAddress.get().getState().getStateCode());
    }

    @Test
    @DisplayName("Should delete address successfully")
    void deleteAddressSuccessfully() {
        // Act
        addressRepository.delete(address1);
        Optional<Address> foundAddress = addressRepository.findById(address1.getId());

        // Assert
        assertFalse(foundAddress.isPresent());
    }

    @Test
    @DisplayName("Should find all addresses")
    void findAllAddressesSuccessfully() {
        // Act
        List<Address> addresses = addressRepository.findAll();

        // Assert
        assertNotNull(addresses);
        assertEquals(3, addresses.size());
        assertThat(addresses)
                .extracting(Address::getId)
                .containsExactlyInAnyOrder(address1.getId(), address2.getId(), address3.getId());
    }
} 