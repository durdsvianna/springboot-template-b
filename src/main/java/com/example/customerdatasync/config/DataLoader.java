package com.example.customerdatasync.config;

import com.example.customerdatasync.model.State;
import com.example.customerdatasync.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final StateRepository stateRepository;

    @Override
    public void run(String... args) {
        if (stateRepository.count() == 0) {
            log.info("Loading initial States data");
            stateRepository.saveAll(getBrazilianStates());
            log.info("Brazilian States loaded successfully");
        }
    }

    private List<State> getBrazilianStates() {
        return Arrays.asList(
            new State("AC", "Acre"),
            new State("AL", "Alagoas"),
            new State("AP", "Amapá"),
            new State("AM", "Amazonas"),
            new State("BA", "Bahia"),
            new State("CE", "Ceará"),
            new State("DF", "Distrito Federal"),
            new State("ES", "Espírito Santo"),
            new State("GO", "Goiás"),
            new State("MA", "Maranhão"),
            new State("MT", "Mato Grosso"),
            new State("MS", "Mato Grosso do Sul"),
            new State("MG", "Minas Gerais"),
            new State("PA", "Pará"),
            new State("PB", "Paraíba"),
            new State("PR", "Paraná"),
            new State("PE", "Pernambuco"),
            new State("PI", "Piauí"),
            new State("RJ", "Rio de Janeiro"),
            new State("RN", "Rio Grande do Norte"),
            new State("RS", "Rio Grande do Sul"),
            new State("RO", "Rondônia"),
            new State("RR", "Roraima"),
            new State("SC", "Santa Catarina"),
            new State("SP", "São Paulo"),
            new State("SE", "Sergipe"),
            new State("TO", "Tocantins")
        );
    }
} 