package com.country.neighbours.visit.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
@Builder
public class TripRequest {

    private String startingCountry;
    private String currency;
    private int budgetPerCountry;

    @Override
    public String toString() {
        return "TripRequest{" +
               "startingCountry='" + startingCountry + '\'' +
               ", currency='" + currency + '\'' +
               ", budgetPerCountry=" + budgetPerCountry +
               ", totalBudget=" + totalBudget +
               '}';
    }

    private int totalBudget;

}
