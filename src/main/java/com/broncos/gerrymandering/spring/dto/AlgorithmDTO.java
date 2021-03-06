package com.broncos.gerrymandering.spring.dto;

import com.broncos.gerrymandering.model.Measure;
import com.broncos.gerrymandering.model.State;
import com.broncos.gerrymandering.model.StateCode;
import com.broncos.gerrymandering.util.StateManager;

import java.util.Map;
import java.util.Set;

/**
 * Created by kristiancharbonneau on 12/3/18.
 */
public class AlgorithmDTO {
    private String username;
    private StateCode stateCode;
    private Map<Measure, Double> weights;
    private Set<Integer> seedIds;
    private Set<Integer> excludedDistricts;
    private String type;
    private String variation;
    private Integer regions;

    public AlgorithmDTO() {
    }

    public Set<Integer> getSeedIds() {
        return seedIds;
    }

    public void setSeedIds(Set<Integer> seedPrecincts) {
        this.seedIds = seedPrecincts;
    }

    public Set<Integer> getExcludedDistricts() {
        return excludedDistricts;
    }

    public void setExcludedDistricts(Set<Integer> excludedDistricts) {
        this.excludedDistricts = excludedDistricts;
    }

    public Integer getRegions() {
        if (this.regions == null) {
            State state = StateManager.getInstance().getState(stateCode);
            this.regions = state.getDistricts().size();
        }
        return this.regions;
    }

    public void setRegions(Integer regions) {
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

    public Map<Measure, Double> getWeights() {
        return weights;
    }

    public void setWeights(Map<Measure, Double> weights) {
        for (Measure measure : Measure.values()) {
            if (!weights.containsKey(measure)) {
                weights.put(measure, 0.0);
            }
        }
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
