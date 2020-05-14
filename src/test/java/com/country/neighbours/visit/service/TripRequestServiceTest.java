package com.country.neighbours.visit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.country.neighbours.visit.model.TripRequest;
import com.country.neighbours.visit.model.TripResult;
import com.country.neighbours.visit.service.impl.TripRequestServiceImpl;

public class TripRequestServiceTest {

    private static TripRequestService tripRequestService;

    private static CurrencyService currencyService;

    private static CountriesService countriesService;

    @BeforeAll
    public static void setup() {
        currencyService = mock(CurrencyService.class);
        countriesService = mock(CountriesService.class);
        tripRequestService = new TripRequestServiceImpl(countriesService, currencyService);
    }

    @Test
    public void testCalculateTrip() {
        when(currencyService.calculateAmountPerNeighbour(anyString(), any(), anyInt())).thenReturn(generateResultPerNeighbour());
        when(countriesService.getNeighbours(anyString())).thenReturn(Arrays.asList("TRK", "GRC"));
        TripResult result = tripRequestService.calculateResult(generateTripRequest());
        assertNotNull(result);
        assertEquals(5, result.getRounds());
        assertEquals("0 BGN", result.getLeftover());
    }

    private TripRequest generateTripRequest() {
        return TripRequest.builder().budgetPerCountry(1000).currency("BGN").startingCountry("Bulgaria")
                .totalBudget(10000).build();
    }

    private Map<String, String> generateResultPerNeighbour() {
        Map<String, String> result = new HashMap<>();
        result.put("Greece", "500.00 EUR");
        result.put("Turkey", "500.00 EUR");
        return result;
    }

}
