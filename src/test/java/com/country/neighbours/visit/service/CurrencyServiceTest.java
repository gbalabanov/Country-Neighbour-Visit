package com.country.neighbours.visit.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.country.neighbours.visit.configuration.TestConfiguration;
import com.country.neighbours.visit.configuration.WireMockInitializer;
import com.country.neighbours.visit.model.CountryInfo;
import com.country.neighbours.visit.service.impl.CurrencyServiceImpl;
import com.country.neighbours.visit.util.TestServiceUtils;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

@TestPropertySource(locations = "classpath:test.properties")
@ContextConfiguration(initializers = {WireMockInitializer.class}, classes = {TestConfiguration.class,
                                                                             CurrencyServiceImpl.class})
@ExtendWith(SpringExtension.class)
public class CurrencyServiceTest {

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private WireMockServer wireMockServer;

    @Test
    public void testCurrencyIsValid() {
        wireMockServer.stubFor(WireMock.get("/latest?base=BGN")
                                       .willReturn(aResponse()
                                                           .withStatus(HttpStatus.OK.value())
                                                           .withHeader(
                                                                   "Content-Type",
                                                                   MediaType.APPLICATION_JSON_VALUE)));
        ReflectionTestUtils.setField(currencyService, "validateCurrencyEndpoint", wireMockServer.baseUrl() + "/latest");
        assertTrue(currencyService.isValid("BGN"));
    }

    @Test
    public void testCalculatePerNeighbour() {
        String currencyApiResponse = TestServiceUtils.loadResource("/example_currency.json");
        wireMockServer.stubFor(WireMock.get("/latest?base=EUR")
                                       .willReturn(aResponse()
                                                           .withStatus(HttpStatus.OK.value())
                                                           .withHeader(
                                                                   "Content-Type",
                                                                   MediaType.APPLICATION_JSON_VALUE)));
        wireMockServer.stubFor(WireMock.get("/latest?base=TL")
                                       .willReturn(aResponse()
                                                           .withStatus(HttpStatus.OK.value())
                                                           .withHeader(
                                                                   "Content-Type",
                                                                   MediaType.APPLICATION_JSON_VALUE)));
        wireMockServer.stubFor(WireMock.get("/latest?base=BGN&symbols=EUR")
                                       .willReturn(aResponse()
                                                            .withBody(currencyApiResponse)
                                                           .withStatus(HttpStatus.OK.value())
                                                           .withHeader(
                                                                   "Content-Type",
                                                                   MediaType.APPLICATION_JSON_VALUE)));
        wireMockServer.stubFor(WireMock.get("/latest?base=BGN&symbols=TL")
                                       .willReturn(aResponse()
                                                           .withBody(currencyApiResponse)
                                                           .withStatus(HttpStatus.OK.value())
                                                           .withHeader(
                                                                   "Content-Type",
                                                                   MediaType.APPLICATION_JSON_VALUE)));
        ReflectionTestUtils.setField(currencyService, "validateCurrencyEndpoint", wireMockServer.baseUrl() + "/latest");
        Map<String, String> result = currencyService.calculateAmountPerNeighbour("BGN", neighboursInfoList(), 1000);
        assertEquals(2, result.size());
        assertThat(result.keySet(), hasItems("Greece", "Turkey"));
        assertThat(result.values(), hasItems("500.00 EUR"));
    }

    private List<CountryInfo> neighboursInfoList() {
        CountryInfo neighbour1 = CountryInfo.builder().alpha3Code("GRC").currencies(Arrays.asList("EUR")).name(
                "Greece").build();
        CountryInfo neighbour2 =
                CountryInfo.builder().alpha3Code("TRK").currencies(Arrays.asList("EUR")).name("Turkey").build();
        return Arrays.asList(neighbour1, neighbour2);
    }

}
