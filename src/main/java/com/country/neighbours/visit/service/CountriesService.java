package com.country.neighbours.visit.service;

import java.util.List;

import com.country.neighbours.visit.model.CountryInfo;

public interface CountriesService {

    /**
     * Get all neighbour countries from an external API.
     * @param country
     * @return list of neighbour country codes
     */
    List<String> getNeighbours(String country);

    /**
     * Helper method to check if the provided country exists.
     * @param country Either code or full name
     * @return true/false
     */
    boolean isValidCountry(String country);

    /**
     * Get info for 1 or more countries.
     * @param countriesCodes one or more country codes, separated by semicolon
     * @return
     */
    List<CountryInfo> getCountriesInfo(String countriesCodes);
}
