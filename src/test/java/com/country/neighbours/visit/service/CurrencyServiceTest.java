package com.country.neighbours.visit.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        this.wireMockServer.stubFor(WireMock.get("/latest?base=BGN")
                                            .willReturn(aResponse()
                                                                .withStatus(HttpStatus.OK.value())
                                                                .withHeader(
                                                                        "Content-Type",
                                                                        MediaType.APPLICATION_JSON_VALUE)));
        ReflectionTestUtils.setField(currencyService, "validateCurrencyEndpoint", wireMockServer.baseUrl() + "/latest");
        assertTrue(currencyService.isValid("BGN"));
    }

}
