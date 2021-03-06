package com.broncos.gerrymandering.spring.dto;

import com.broncos.gerrymandering.model.District;
import com.broncos.gerrymandering.model.Election;
import com.broncos.gerrymandering.model.State;
import com.broncos.gerrymandering.util.BoundaryWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by kristiancharbonneau on 12/6/18.
 */
public class AlgorithmUpdateDTO {
    public Map<Integer, BoundaryWrapper> districtBoundaries;
    public Map<Integer, Election> elections;
    double objFuncVal;
    boolean isTerminated;

    public AlgorithmUpdateDTO() {
        districtBoundaries = new HashMap<>();
        elections = new HashMap<>();
    }

    public AlgorithmUpdateDTO(Set<District> districts, double objFuncVal, boolean isTerminated) {
        districtBoundaries = new HashMap<>();
        elections = new HashMap<>();
        for (District district: districts) {
            districtBoundaries.put(district.getDistrictId(), new BoundaryWrapper(district.getBoundary()));
            if(isTerminated)
                elections.put(district.getDistrictId(), district.getElectionByYear().get((short) 2010));
        }
        this.objFuncVal = objFuncVal;
        this.isTerminated = isTerminated;
    }

    public Map<Integer, BoundaryWrapper> getDistrictBoundaries() {
        return districtBoundaries;
    }

    public void setDistricts(Map<Integer, BoundaryWrapper> districtBoundaries) {
        this.districtBoundaries = districtBoundaries;
    }

    public double getObjFuncVal() {
        return objFuncVal;
    }

    public void setObjFuncVal(double objFuncVal) {
        this.objFuncVal = objFuncVal;
    }

    public boolean isTerminated() { return isTerminated; }

    public void setTerminated(boolean terminated) { isTerminated = terminated; }
}
