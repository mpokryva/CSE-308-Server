package com.broncos.gerrymandering.gerrymandering;

import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * Created by mpokr on 11/12/2018.
 */
@RestController
public class Controller {

    private static final String PROPERTIES_KEY = "properties";
    private static final String DISTRICT_ID_KEY = "district_id";

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value = "name") String name) {
        return "Hello " + name;
    }

    @RequestMapping(value = "/precinct-info",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getPrecinctInfo(@RequestParam(value = "geoid") String geoid) throws Exception {
        final String pathname = "data/ny_precs_info.json";
        JSONObject precinctsInfo = getJsonFromFile(pathname);
        return precinctsInfo.getJSONObject(geoid).toString();
    }

    @RequestMapping(value = "/precinct-geo",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getPrecinctBoundaries(@RequestParam(value = "districtid") String districtid) throws Exception {
        final String pathname = "data/ny_precs_geo.json";
        JSONObject precinctsBoundaries = getJsonFromFile(pathname);
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
        JSONObject districtsInfo = getJsonFromFile(pathname);
        return districtsInfo.getJSONObject(districtid).toString();
    }

    @RequestMapping(value = "/district-geo",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getDistrictBoundaries(@RequestParam(value = "statecode") String statecode) throws Exception {
        final String pathname = "data/ny_dist_geo.json";
        JSONObject districtsBoundaries = getJsonFromFile(pathname);
        return districtsBoundaries.toString();
    }

    @RequestMapping(value = "/state-info",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getStateInfo(@RequestParam(value = "statecode") String statecode) throws Exception {
        final String pathname = "data/ny_info.json";
        JSONObject stateInfo = getJsonFromFile(pathname);
        return stateInfo.toString();
    }

    @RequestMapping(value = "/state-geo",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getStateBoundaries(@RequestParam(value = "statecode") String statecode) throws Exception {
        final String pathname = "data/ny_geo.json";
        JSONObject stateBoundaries = getJsonFromFile(pathname);
        return stateBoundaries.toString();
    }

    private JSONObject getJsonFromFile(String pathname) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(pathname)));
            return new JSONObject(content);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}