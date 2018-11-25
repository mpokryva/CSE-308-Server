package com.broncos.gerrymandering.spring.controller;

import com.broncos.gerrymandering.model.Election;
import com.broncos.gerrymandering.model.Precinct;
import com.broncos.gerrymandering.model.StateCode;
import com.broncos.gerrymandering.util.DefaultEntityManager;
import com.broncos.gerrymandering.util.StateManager;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import java.util.Map;

/**
 * Created by mpokr on 11/25/2018.
 */
@RestController
public class InfoController {

    @RequestMapping(value = "/precinct-info",
            method = RequestMethod.GET,
            params = {"precinctId", "districtId", "stateCode"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Short, Election> getPrecinctInfo(int precinctId, int districtId, StateCode stateCode) {
        StateManager sm = StateManager.getInstance();
        Precinct precinct = sm.getPrecinct(precinctId, districtId, stateCode);
        if (precinct == null) {
            return null;
        }
        return precinct.getElectionByYear();
    }


}
