package com.broncos.gerrymandering.spring.controller;

import com.broncos.gerrymandering.model.*;
import com.broncos.gerrymandering.util.StateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * Created by mpokr on 11/25/2018.
 */
@RestController
public class InfoController {


    @RequestMapping(value = "/state-info",
            method = RequestMethod.GET,
            params = {"stateCode"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Short, Election> getStateInfo(StateCode stateCode) {
        StateManager sm = StateManager.getInstance();
        State state = sm.getState(stateCode);
        return state.getElectionByYear();
    }

    @RequestMapping(value = "/district-info",
            method = RequestMethod.GET,
            params = {"districtId", "stateCode"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Short, Election> getDistrictInfo(Integer districtId, StateCode stateCode) {
        StateManager sm = StateManager.getInstance();
        District district = sm.getDistrict(districtId, stateCode);
        if (district == null) {
            return null;
        }
        return district.getElectionByYear();
    }

    @RequestMapping(value = "/precinct-info",
            method = RequestMethod.GET,
            params = {"precinctId", "districtId", "stateCode"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Short, Election> getPrecinctInfo(Integer precinctId, Integer districtId, StateCode stateCode) {
        StateManager sm = StateManager.getInstance();
        Precinct precinct = sm.getPrecinct(precinctId, districtId, stateCode);
        if (precinct == null) {
            return null;
        }
        return precinct.getElectionByYear();
    }






}
