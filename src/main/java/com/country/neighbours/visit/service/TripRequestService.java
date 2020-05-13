package com.country.neighbours.visit.service;

import com.country.neighbours.visit.model.TripRequest;
import com.country.neighbours.visit.model.TripResult;

public interface TripRequestService {

    /**
     * Calculate trip based on provided parameters
     *
     * @param request {@link TripRequest} object
     * @return {@link TripResult}
     */
    TripResult calculateResult(TripRequest request);

}
