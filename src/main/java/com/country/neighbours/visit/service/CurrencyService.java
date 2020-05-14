package com.country.neighbours.visit.service;

import java.util.List;
import java.util.Map;

import com.country.neighbours.visit.model.CountryInfo;

/**
 * Service for interacting with currencies API
 */
public interface CurrencyService {

    /**
     * Check if the provided currency is a valid one
     * @param currency
     * @return
     */
    boolean isValid(String currency);

    /**
     * Calculate the amount per country
     * @param from Base currency
     * @param neighboursList List with neighbour countries
     * @param amount the amount money per country
     * @return Map with key country code and value - amount for that country
     */
    Map<String, String> calculateAmountPerNeighbour(String from, List<CountryInfo> neighboursList,
                                                    int amount);
}
