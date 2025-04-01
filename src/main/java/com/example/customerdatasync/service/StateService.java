package com.example.customerdatasync.service;

import com.example.customerdatasync.dto.StateDto;
import com.example.customerdatasync.model.State;

import java.util.List;

public interface StateService {
    List<StateDto> getAllStates();
    State getStateByCode(String stateCode);
    StateDto getStateDetails(String stateCode);
} 