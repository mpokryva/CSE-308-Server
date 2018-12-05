package com.broncos.gerrymandering.spring.dto;

import com.broncos.gerrymandering.model.Measure;
import com.broncos.gerrymandering.model.StateCode;

import java.util.Map;

/**
 * Created by kristiancharbonneau on 12/3/18.
 */
public class AlgorithmDTO {
    private String username;
    private StateCode stateCode;
    private Map<Measure, Double> weights;
    private String type;
    private String variation;
    private int regions;

    public AlgorithmDTO() {
    }

    public int getRegions() {
        return regions;
    }

    public void setRegions(int regions) {
        this.regions = regions;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public StateCode getStateCode() {
        return stateCode;
    }

    public void setStateCode(StateCode stateCode) {
        this.stateCode = stateCode;
    }

    public Map<Measure, Double> getWeights() { return weights; }

    public void setWeights(Map<Measure, Double> weights) {
        this.weights = weights;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }

}
