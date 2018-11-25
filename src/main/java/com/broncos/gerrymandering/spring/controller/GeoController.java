package com.broncos.gerrymandering.spring.controller;

import com.broncos.gerrymandering.model.District;
import com.broncos.gerrymandering.model.Precinct;
import com.broncos.gerrymandering.model.StateCode;
import com.broncos.gerrymandering.util.StateManager;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mpokr on 11/25/2018.
 */
@RestController
public class GeoController {

    @RequestMapping(value = "/precinct-geo",
            method = RequestMethod.GET,
            params = {"districtId", "stateCode"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Integer, String> getPrecinctGeo(int districtId, StateCode stateCode) {
        StateManager sm = StateManager.getInstance();
        District district = sm.getDistrict(districtId, stateCode);
        if (district == null) {
            return null;
        }
        Map<Integer, Precinct> precinctById = district.getPrecinctById();
        Map<Integer, String> boundaryByPrecinctId = new HashMap<>();
        for (Precinct precinct : precinctById.values()) {
            boundaryByPrecinctId.put(precinct.getPrecinctId(), precinct.getBoundary());
        }
        return boundaryByPrecinctId;
    }

}
