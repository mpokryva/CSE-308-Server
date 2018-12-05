package com.broncos.gerrymandering.algorithm;

import com.broncos.gerrymandering.model.*;
import com.broncos.gerrymandering.util.StateManager;

import java.util.List;
import java.util.Queue;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Algorithm {

    private Queue<Move> pastMoves;
    private Map<Measure, Double> weights;
    private boolean terminated;
    private StateCode stateCode;
    private State redistrictedState;
    private State initialState;
    private int numSteps;

    public Algorithm(StateCode stateCode, Map<Measure, Double> weights) {
        this.stateCode = stateCode;
        this.setInitialState(StateManager.getInstance().getState(stateCode));
        this.weights = weights;
        pastMoves = new ConcurrentLinkedQueue<>();
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

    protected Map<Measure, Double> getWeights() {
        return weights;
    }

    protected void setWeights(Map<Measure, Double> weights) {
        this.weights = weights;
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

    public void addMove(Move move) {
        pastMoves.add(move);
    }

    public Move[] flushPastMoves() {
        Move[] moves = new Move[0];
        synchronized (pastMoves) {
            moves = pastMoves.toArray(moves);
            pastMoves.clear();
        }
        return moves;
    }
}
