package com.country.neighbours.visit.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.country.neighbours.visit.model.CountryInfo;
import com.country.neighbours.visit.model.TripRequest;
import com.country.neighbours.visit.model.TripResult;
import com.country.neighbours.visit.service.CountriesService;
import com.country.neighbours.visit.service.CurrencyService;
import com.country.neighbours.visit.service.TripRequestService;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripRequestServiceImpl implements TripRequestService {

    private final CountriesService countriesService;
    private final CurrencyService currencyService;

    @Override
    public TripResult calculateResult(TripRequest request) {
        log.info(("Calculating trip..."));
        List<String> neighboursCodes = countriesService.getNeighbours(request.getStartingCountry());
        log.info("{} has {} neighbours.", request.getStartingCountry(), neighboursCodes.size());
        //combine requests for all neighbours into one to save time - the external api allows it
        String codesConcatenated = neighboursCodes.stream().collect(Collectors.joining(";"));
        List<CountryInfo> neighboursInfo;
        int leftover;
        int visitRounds;
        Map outputPerNeighbour;
        if (neighboursCodes.size() > 0) {
            neighboursInfo = countriesService.getCountriesInfo(codesConcatenated);
            visitRounds = request.getTotalBudget() / (neighboursCodes.size() * request.getBudgetPerCountry());
            leftover =
                    request.getTotalBudget() - ((neighboursCodes.size() * request.getBudgetPerCountry()) * visitRounds);

            outputPerNeighbour = currencyService.calculateAmountPerNeighbour(request.getCurrency(), neighboursInfo,
                                                                             request.getBudgetPerCountry());
        } else {
            visitRounds = 0;
            leftover = request.getTotalBudget();
            outputPerNeighbour = Collections.EMPTY_MAP;
        }

        String leftoverString = String.format("%d %s", leftover, request.getCurrency());
        log.info("Trip calculated!");
        return TripResult.builder()
                .rounds(visitRounds)
                .leftover(leftoverString)
                .budgetPerCountry(outputPerNeighbour)
                .build();
    }

    @Data
    static class NeighboursInfo {
        String alpha3Code;
        Map<Integer, String> currencies;
    }
}
