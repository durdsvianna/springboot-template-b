package com.example.customerdatasync.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "states")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class State {
    
    @Id
    @Column(length = 2)
    private String stateCode;
    
    @Column(nullable = false)
    private String name;
} 