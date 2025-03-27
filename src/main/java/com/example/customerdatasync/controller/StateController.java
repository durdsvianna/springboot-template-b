package com.example.customerdatasync.controller;

import com.example.customerdatasync.dto.StateDto;
import com.example.customerdatasync.service.StateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ufs")
@RequiredArgsConstructor
@Tag(name = "State", description = "API for State management")
public class StateController {

    private final StateService stateService;

    @GetMapping
    @Operation(summary = "List all States of Brazil",
            description = "Endpoint to list all 27 Brazilian States")
    @ApiResponse(responseCode = "200", description = "List of all 27 States of Brazil")
    public ResponseEntity<List<StateDto>> getAllStates() {
        return ResponseEntity.ok(stateService.getAllStates());
    }
} 