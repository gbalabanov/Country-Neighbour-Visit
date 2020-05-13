package com.country.neighbours.visit.service.impl;

import static com.country.neighbours.visit.util.Constants.RAPIDAPI_KEY_NAME;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.country.neighbours.visit.model.CountryInfo;
import com.country.neighbours.visit.service.CountriesService;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
@Data
public class CountriesServiceImpl implements CountriesService {

    private final RestTemplate restTemplate;

    @Value("${rapidapi.key}")
    private String countriesApiKey;

    @Value("${rapidapi.name.endpoint}")
    private String countiesApiByName;

    @Value("${rapidapi.alpha.endpoint}")
    private String countiesApiByCode;

    @Override
    @Cacheable(value = "mainCache")
    public List<String> getNeighbours(String country) {
        List<String> res;
        HttpHeaders headers = new HttpHeaders();
        headers.set(RAPIDAPI_KEY_NAME, countriesApiKey);
        HttpEntity entityHeaders = new HttpEntity(headers);
        //check if it is a code or full name
        String countryApi = country.length() < 4 ? countiesApiByCode : countiesApiByName;

        ResponseEntity<CountryInfo[]> result = restTemplate.exchange(countryApi + "/" + country, HttpMethod.GET,
                                                                     entityHeaders,
                                                                     CountryInfo[].class);

        if (result.getBody().length == 1) {
            res = result.getBody()[0].getBorders();
        } else {
            res = Arrays.asList(result.getBody()).stream().filter(c -> c.getName().equalsIgnoreCase(country))
                    .map(c -> c.getBorders()).findFirst().orElseThrow(RuntimeException::new);
        }

        return res;
    }

    @Override
    public boolean isValidCountry(String country) {
        log.info("Validating country {}", country);
        HttpHeaders headers = new HttpHeaders();
        headers.set(RAPIDAPI_KEY_NAME, countriesApiKey);
        HttpEntity entityHeaders = new HttpEntity(headers);
        //check if it is a code or full name
        String countryApi = country.length() < 4 ? countiesApiByCode : countiesApiByName;
        try {
            restTemplate.exchange(countryApi + "/" + country, HttpMethod.GET, entityHeaders,
                                  Void.class);
        } catch (HttpClientErrorException e) {
            log.warn("Invalid base country {}", country);
            return false;
        }
        log.info("Country {} is valid.", country);
        return true;
    }

    @Override
    public List<CountryInfo> getCountriesInfo(String countriesCodes) {
        log.info("Getting info for countries {}", countriesCodes);
        HttpHeaders headers = new HttpHeaders();
        headers.set(RAPIDAPI_KEY_NAME, countriesApiKey);
        HttpEntity entityHeaders = new HttpEntity(headers);
        //check if it is a code or full name
        String endpoint =
                UriComponentsBuilder.fromHttpUrl(countiesApiByCode).queryParam("codes", countriesCodes).toUriString();

        ResponseEntity<CountryInfo[]> result = restTemplate.exchange(endpoint, HttpMethod.GET,
                                                                     entityHeaders,
                                                                     CountryInfo[].class);
        log.info("Info for countries {} retrieved.", countriesCodes);
        return Arrays.stream(result.getBody()).collect(Collectors.toList());
    }
}
