package com.broncos.gerrymandering.algorithm;

import com.broncos.gerrymandering.model.*;
import com.broncos.gerrymandering.util.StateManager;

import java.util.Collection;
import java.util.Map;

public abstract class Algorithm {

    private Collection<Move> pastMoves;
    private Map<Measure, Double> weights;
    private boolean terminated;
    private StateCode stateCode;
    private State redistrictedState;
    private State initialState;
    private int numSteps;

    public Algorithm() {

    }

    public Algorithm(StateCode stateCode) {

    }

    public abstract State run();

    protected double getObjFuncValueByDistrict(District district, Map<Measure, Double> weights) {
        return redistrictedState.getDistrictById(district.getDistrictId()).calculateObjFuncValue(weights);
    }

    protected boolean isTerminated() {
        return isTerminated();
    }

    protected int getNumSteps() {
        return numSteps;
    }

    public Collection<Move> getPastMoves() {
        return pastMoves;
    }

    protected Map<Measure, Double> getWeights() {
        return weights;
    }

    protected StateCode getStateCode() {
        return stateCode;
    }

    protected State getRedistrictedState() {
        return redistrictedState;
    }

    protected State getInitialState() {
        return initialState;
    }

    protected void setInitialState(State initialState) {
        this.initialState = initialState;
    }

    protected void setRedistrictedState(State redistrictedState) {
        this.redistrictedState = redistrictedState;
    }
}
