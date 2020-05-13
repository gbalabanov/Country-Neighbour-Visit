package com.country.neighbours.visit.mapping;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.country.neighbours.visit.configuration.Configuration;
import com.country.neighbours.visit.model.CountryInfo;
import com.country.neighbours.visit.util.TestServiceUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@TestPropertySource(locations = "classpath:test.properties")
@ContextConfiguration(classes = {Configuration.class})
@ExtendWith(SpringExtension.class)
public class POJOMappingTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testReadCountryInfo() throws JsonProcessingException {
        String countryInfoJson = TestServiceUtils.loadResource("/example_country.json");
        CountryInfo[] countryInfo = objectMapper.readValue(countryInfoJson, CountryInfo[].class);
        assertEquals(1, countryInfo.length);
        CountryInfo countryInfo1 = countryInfo[0];
        assertEquals("Bulgaria", countryInfo1.getName());
        assertEquals("BGR", countryInfo1.getAlpha3Code());
        assertEquals(5, countryInfo1.getBorders().size());
        assertEquals(1, countryInfo1.getCurrencies().size());
        assertThat(countryInfo1.getCurrencies(), hasItem("BGN"));
        assertThat(countryInfo1.getBorders(), containsInAnyOrder("GRC","MKD","ROU","SRB","TUR"));
    }
}
