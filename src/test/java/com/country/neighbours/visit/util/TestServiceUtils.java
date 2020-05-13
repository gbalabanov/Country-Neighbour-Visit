package com.country.neighbours.visit.util;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

public class TestServiceUtils {

    /**
     * Load resource.
     *
     * @param path relative path to the resource
     * @return the resource as String
     */
    public static String loadResource(String path) {
        String result;

        InputStream inputStream = null;
        try {
            ClassPathResource cpr = new ClassPathResource(path);
            inputStream = cpr.getInputStream();
            result = IOUtils.toString(inputStream);
        } catch (IOException e) {
            // fail the test if we can't load the data
            fail("Unable to laod rest resource " + path);
            result = "";
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return result;
    }

}
