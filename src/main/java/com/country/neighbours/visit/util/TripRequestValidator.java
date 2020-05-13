package com.country.neighbours.visit.util;

import org.springframework.stereotype.Service;

import com.country.neighbours.visit.model.TripRequest;
import com.country.neighbours.visit.model.TripRequestDTO;
import com.country.neighbours.visit.model.TripResult;
import com.country.neighbours.visit.service.CountriesService;
import com.country.neighbours.visit.service.CurrencyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TripRequestValidator {

    private final CurrencyService currencyService;
    private final CountriesService countriesService;

    public void validate(TripRequestDTO request) {
        log.info("Validating trip request {}", request);
        checkNumericValues(request);
        if (!currencyService.isValid(request.getCurrency()) ||
            !countriesService.isValidCountry(request.getStartingCountry()) ||
            !isEnoughBudget(request)) {
            throw new IllegalArgumentException("Invalid parameter(s). Please verify!");
        }
        log.info("Request {} is valid !", request);
    }

    private boolean isEnoughBudget(TripRequestDTO request) {
        return Integer.parseInt(request.getTotalBudget()) >= countriesService.getNeighbours(request.getStartingCountry()).size() * Integer.parseInt(request.getBudgetPerCountry());
    }

    private void checkNumericValues(TripRequestDTO request) {
        try {
            Integer.parseInt(request.getBudgetPerCountry());
            Integer.parseInt(request.getTotalBudget());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Both totalBudget and budgetPerCountry should be numbers !");
        }
    }
}
