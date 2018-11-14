package com.broncos.gerrymandering.gerrymandering;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
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
            produces = "application/json")
    public String getPrecinctInfo(@RequestParam(value = "geoid") String geoid) throws Exception {
        final String pathname = "data/ny_precs_info.json";
        JSONObject precinctsInfo = getJsonFromFile(pathname);
        return precinctsInfo.getJSONObject(geoid).toString();
    }

    @RequestMapping(value = "/precinct-geo",
            method = RequestMethod.GET,
            produces = "application/json")
    public String getPrecinctBoundaries(@RequestParam(value = "districtid") String districtid) throws Exception {
        final String pathname = "data/ny_precs_geo.json";
        JSONObject precinctsBoundaries = getJsonFromFile(pathname);
        JSONObject response = new JSONObject();
        Iterator<String> it = precinctsBoundaries.keys();
        while(it.hasNext()) {
            String key = it.next();
            JSONObject precinctBoundaries = precinctsBoundaries.getJSONObject(key);
            JSONObject properties = precinctBoundaries.getJSONObject(PROPERTIES_KEY);
            System.out.println(properties);
            if (properties != null && properties.getString(DISTRICT_ID_KEY).equals(districtid)) {
                response.append(key, precinctBoundaries);
            }
        }
        return response.toString();
    }


    private JSONObject getJsonFromFile(String pathname) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String content = new String(Files.readAllBytes(Paths.get(pathname)));
            return new JSONObject(content);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}