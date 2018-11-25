package com.broncos.gerrymandering.util;

import org.json.JSONObject;
import org.locationtech.jts.geom.Geometry;
import org.wololo.jts2geojson.GeoJSONReader;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by mpokr on 11/14/2018.
 */
public class JSONParser {

    private static String NY_PRECINCTS_GEO_FILE = "data/ny_precs_geo.json";

    public Geometry geoJsonToGeometry(String geoJson) {
        GeoJSONReader reader = new GeoJSONReader();
        Geometry geometry = reader.read(geoJson);
        return geometry;
    }

    public JSONObject getJsonFromFile(String pathname) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(pathname)));
            return new JSONObject(content);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        JSONParser parser = new JSONParser();
        JSONObject json = parser.getJsonFromFile(NY_PRECINCTS_GEO_FILE);
        final String geoid = "36001263";
        String geoJson = json.getJSONObject(geoid).getJSONObject("geometry").toString();
        System.out.println(geoJson);
        Geometry geometry = parser.geoJsonToGeometry(geoJson);
        System.out.println(geometry);


    }

}
