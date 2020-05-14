package com.country.neighbours.visit.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.country.neighbours.visit.model.CountryInfo;
import com.country.neighbours.visit.service.CurrencyService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    @Value("${exchange.api.validate}")
    private String validateCurrencyEndpoint;

    private final RestTemplate restTemplate;

    @Override
    @Cacheable(value = "mainCache")
    public boolean isValid(String currency) {
        boolean result = true;
        String endpoint =
                UriComponentsBuilder.fromHttpUrl(validateCurrencyEndpoint).queryParam("base", currency).build()
                        .toUriString();
        try {
            restTemplate.getForEntity(endpoint, Void.class);
        } catch (HttpClientErrorException e) {
            log.warn("Currency {} not found.", currency);
            result = false;
        }
        return result;
    }

    private double exchange(String from, String to, int amount) {
        String endpoint =
                UriComponentsBuilder.fromHttpUrl(validateCurrencyEndpoint).queryParam("base", from).queryParam(
                        "symbols",to).toUriString();
        ResponseEntity<Rates> response = restTemplate.getForEntity(endpoint, Rates.class);
        return amount * response.getBody().getRates().get(to);
    }

    @Override
    public Map<String, String> calculateAmountPerNeighbour(String from, List<CountryInfo> neighboursList,
                                                           int amount) {
        return neighboursList.stream().parallel().collect(Collectors.toMap(CountryInfo::getName, c -> {
            String to = from;
            double converted = amount;
            for (String curr : c.getCurrencies()){
                log.info("Check if {} currency {} is valid.", c.getName(), curr);
                if (isValid(curr)) {
                    to = curr;
                }
            }
            log.info("Currency for {} - {}", c.getName(), to);
            if (!to.equals(from)) {
                converted = exchange(from, to, amount);
            }

            return String.format("%.2f %s", converted, to);
        }));

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Rates {
        private Map<String, Double> rates;
    }
}
