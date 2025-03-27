package com.example.customerdatasync.repository;

import com.example.customerdatasync.model.Address;
import com.example.customerdatasync.model.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByState(State state);
} 