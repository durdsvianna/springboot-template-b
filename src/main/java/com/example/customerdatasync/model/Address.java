package com.example.customerdatasync.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "addresses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String street;
    
    private String complement;
    
    @Column(nullable = false, length = 9)
    private String zipCode;
    
    @Column(nullable = false)
    private String city;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "state_code", nullable = false)
    private State state;
} 