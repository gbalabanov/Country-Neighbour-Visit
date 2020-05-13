package com.country.neighbours.visit.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
@Builder
public class TripRequestDTO {

    private String startingCountry;
    private String currency;
    private String budgetPerCountry;
    private String totalBudget;

    @Override
    public String toString() {
        return "TripRequestDTO{" +
               "startingCountry='" + startingCountry + '\'' +
               ", currency='" + currency + '\'' +
               ", budgetPerCountry=" + budgetPerCountry +
               ", totalBudget=" + totalBudget +
               '}';
    }
}
