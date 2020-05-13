package com.country.neighbours.visit.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.country.neighbours.visit.configuration.TestConfiguration;
import com.country.neighbours.visit.configuration.WireMockInitializer;
import com.country.neighbours.visit.model.CountryInfo;
import com.country.neighbours.visit.service.impl.CountriesServiceImpl;
import com.country.neighbours.visit.util.TestServiceUtils;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

@TestPropertySource(locations = "classpath:test.properties")
@ContextConfiguration(initializers = {WireMockInitializer.class}, classes = {TestConfiguration.class,
                                                                             CountriesServiceImpl.class})
@ExtendWith(SpringExtension.class)
public class CountryServiceTest {

    @Autowired
    private CountriesService countriesService;

    @Autowired
    private WireMockServer wireMockServer;

    @Test
    public void testGetNeighbours() {
        String response = TestServiceUtils.loadResource("/example_country.json");
        this.wireMockServer.stubFor(WireMock.get("/BGN")
                                            .willReturn(aResponse()
                                                                .withBody(response)
                                                                .withHeader(
                                                                        "Content-Type",
                                                                        MediaType.APPLICATION_JSON_VALUE)));

        List<String> neighbours = countriesService.getNeighbours("BGN");
        assertEquals(5, neighbours.size());
        assertThat(neighbours, containsInAnyOrder("GRC", "MKD", "ROU", "SRB", "TUR"));
    }

    @Test
    public void testGetCountryInfo() {
        String response = TestServiceUtils.loadResource("/example_country.json");
        this.wireMockServer.stubFor(WireMock.get("/alpha?codes=BGN")
                                            .willReturn(aResponse()
                                                                .withBody(response)
                                                                .withHeader(
                                                                        "Content-Type",
                                                                        MediaType.APPLICATION_JSON_VALUE)));
        ReflectionTestUtils.setField(countriesService, "countiesApiByCode", wireMockServer.baseUrl() + "/alpha");
        List<CountryInfo> countryInfoList = countriesService.getCountriesInfo("BGN");
        assertEquals(1, countryInfoList.size());
        CountryInfo country = countryInfoList.get(0);
        assertEquals("Bulgaria", country.getName());
    }

    @Test
    public void testNoNeighbours() {
        String response = TestServiceUtils.loadResource("/example_country_no_neighbours.json");
        this.wireMockServer.stubFor(WireMock.get("/Japan")
                                            .willReturn(aResponse()
                                                                .withBody(response)
                                                                .withHeader(
                                                                        "Content-Type",
                                                                        MediaType.APPLICATION_JSON_VALUE)));
        List<String> countryInfoList = countriesService.getNeighbours("Japan");
        assertEquals(0, countryInfoList.size());
    }

    @Test
    public void testNoCountryShouldThrowException() {
        String response = TestServiceUtils.loadResource("/example_no_country.json");
        this.wireMockServer.stubFor(WireMock.get("/BGN")
                                            .willReturn(aResponse()
                                                                .withBody(response)
                                                                .withHeader(
                                                                        "Content-Type",
                                                                        MediaType.APPLICATION_JSON_VALUE)));

        assertThrows(RuntimeException.class, () -> countriesService.getNeighbours("BGN"));
    }
}
