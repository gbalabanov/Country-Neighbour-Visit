package com.country.neighbours.visit.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.github.tomakehurst.wiremock.WireMockServer;

public class TestConfiguration {

    @Autowired
    WireMockServer wireMockServer;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().rootUri(wireMockServer.baseUrl()).build();
    }


}
