package com.country.neighbours.visit.model;

import java.util.Map;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Builder;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
public class TripResult {

    private Map<String, String> budgetPerCountry;
    private int rounds;
    private String leftover;
}
