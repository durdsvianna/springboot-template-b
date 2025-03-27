package com.example.customerdatasync.service.impl;

import com.example.customerdatasync.dto.StateDto;
import com.example.customerdatasync.exception.ResourceNotFoundException;
import com.example.customerdatasync.model.State;
import com.example.customerdatasync.repository.StateRepository;
import com.example.customerdatasync.service.StateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StateServiceImpl implements StateService {

    private final StateRepository stateRepository;

    @Override
    public List<StateDto> getAllStates() {
        return stateRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public State getStateByCode(String stateCode) {
        return stateRepository.findById(stateCode)
                .orElseThrow(() -> new ResourceNotFoundException("State", "code", stateCode));
    }
    
    private StateDto mapToDto(State state) {
        return new StateDto(state.getStateCode(), state.getName());
    }
} 