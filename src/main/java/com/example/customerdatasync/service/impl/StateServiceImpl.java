package com.example.customerdatasync.service.impl;

import com.example.customerdatasync.dto.StateDto;
import com.example.customerdatasync.exception.ResourceNotFoundException;
import com.example.customerdatasync.exception.ServiceException;
import com.example.customerdatasync.model.State;
import com.example.customerdatasync.repository.StateRepository;
import com.example.customerdatasync.service.StateService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StateServiceImpl implements StateService {

    private final StateRepository stateRepository;

    @Override
    public List<StateDto> getAllStates() {
        try {
            return stateRepository.findAll()
                    .stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to retrieve all states", "getAllStates", e);
        } catch (Exception e) {
            throw new ServiceException("Unexpected error retrieving all states", e);
        }
    }

    @Override
    public State getStateByCode(String stateCode) {
        try {
            return stateRepository.findById(stateCode)
                    .orElseThrow(() -> new ResourceNotFoundException("State", "stateCode", stateCode));
        } catch (ResourceNotFoundException e) {
            // Re-throw resource not found exceptions as is
            throw e;
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to retrieve state with code: " + stateCode, "getStateByCode", e);
        } catch (Exception e) {
            throw new ServiceException("Unexpected error retrieving state with code: " + stateCode, e);
        }
    }

    @Override
    public StateDto getStateDetails(String stateCode) {
        try {
            State state = getStateByCode(stateCode);
            return mapToDto(state);
        } catch (ResourceNotFoundException e) {
            // Re-throw resource not found exceptions as is
            throw e;
        } catch (ServiceException e) {
            // Re-throw service exceptions as is
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Unexpected error retrieving state details for code: " + stateCode, e);
        }
    }
    
    private StateDto mapToDto(State state) {
        return new StateDto(
                state.getStateCode(),
                state.getName()
        );
    }
} 