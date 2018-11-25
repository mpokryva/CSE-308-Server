package com.broncos.gerrymandering.controller;

import com.broncos.gerrymandering.util.JSONParser;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;

/**
 * Created by mpokr on 11/12/2018.
 */
@RestController
public class Controller {

    private static final String PROPERTIES_KEY = "properties";
    private static final String DISTRICT_ID_KEY = "district_id";
    private JSONParser jsonParser = new JSONParser();

    @RequestMapping(value = "/precinct-info",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getPrecinctInfo(@RequestParam(value = "geoid") String geoid) throws Exception {
        final String pathname = "data/ny_precs_info.json";
        JSONObject precinctsInfo = jsonParser.getJsonFromFile(pathname);
        return precinctsInfo.getJSONObject(geoid).toString();
    }

    @RequestMapping(value = "/precinct-geo",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getPrecinctBoundaries(@RequestParam(value = "districtid") String districtid) throws Exception {
        final String pathname = "data/ny_precs_geo.json";
        JSONObject precinctsBoundaries = jsonParser.getJsonFromFile(pathname);
        JSONObject response = new JSONObject();
        Iterator<String> it = precinctsBoundaries.keys();
        while (it.hasNext()) {
            String key = it.next();
            JSONObject precinctBoundaries = precinctsBoundaries.getJSONObject(key);
            JSONObject properties = precinctBoundaries.getJSONObject(PROPERTIES_KEY);
            if (properties != null && properties.getString(DISTRICT_ID_KEY).equals(districtid)) {
                response.append(key, precinctBoundaries);
            }
        }
        return response.toString();
    }

    @RequestMapping(value = "/district-info",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getDistrictInfo(@RequestParam(value = "districtid") String districtid) throws Exception {
        final String pathname = "data/ny_dist_info.json";
        JSONObject districtsInfo = jsonParser.getJsonFromFile(pathname);
        return districtsInfo.getJSONObject(districtid).toString();
    }

    @RequestMapping(value = "/district-geo",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getDistrictBoundaries(@RequestParam(value = "statecode") String statecode) throws Exception {
        final String pathname = "data/ny_dist_geo.json";
        JSONObject districtsBoundaries = jsonParser.getJsonFromFile(pathname);
        return districtsBoundaries.toString();
    }

    @RequestMapping(value = "/state-info",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getStateInfo(@RequestParam(value = "statecode") String statecode) throws Exception {
        final String pathname = "data/ny_info.json";
        JSONObject stateInfo = jsonParser.getJsonFromFile(pathname);
        return stateInfo.toString();
    }

    @RequestMapping(value = "/state-geo",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getStateBoundaries(@RequestParam(value = "statecode") String statecode) throws Exception {
        final String pathname = "data/ny_geo.json";
        JSONObject stateBoundaries = jsonParser.getJsonFromFile(pathname);
        return stateBoundaries.toString();
    }

}