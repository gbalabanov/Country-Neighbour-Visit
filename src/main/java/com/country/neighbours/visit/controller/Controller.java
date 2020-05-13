package com.country.neighbours.visit.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.country.neighbours.visit.model.TripRequest;
import com.country.neighbours.visit.model.TripRequestDTO;
import com.country.neighbours.visit.model.TripResult;
import com.country.neighbours.visit.service.TripRequestService;
import com.country.neighbours.visit.util.TripRequestValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/")
public class Controller {

    private final TripRequestValidator validator;
    private final TripRequestService tripRequestService;

    @RequestMapping(value = "/request", method= RequestMethod.GET)
    public ResponseEntity<TripResult> request(@RequestParam String startingCountry, @RequestParam String currency,
                                              @RequestParam String budgetPerCountry, @RequestParam String totalBudget
            , @AuthenticationPrincipal OAuth2User principal) {
        TripRequestDTO requestDTO = TripRequestDTO.builder()
                .startingCountry(startingCountry)
                .currency(currency)
                .budgetPerCountry(budgetPerCountry)
                .totalBudget(totalBudget)
                .build();
        validator.validate(requestDTO);
        TripRequest request = TripRequest.builder()
                .currency(requestDTO.getCurrency())
                .startingCountry(requestDTO.getStartingCountry())
                .budgetPerCountry(Integer.parseInt(requestDTO.getBudgetPerCountry()))
                .totalBudget(Integer.parseInt(requestDTO.getTotalBudget())).build();
        TripResult result = tripRequestService.calculateResult(request);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }
}
