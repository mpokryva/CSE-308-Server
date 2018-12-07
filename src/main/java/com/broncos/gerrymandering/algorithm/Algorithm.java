package com.broncos.gerrymandering.algorithm;

import com.broncos.gerrymandering.model.*;
import com.broncos.gerrymandering.util.StateManager;

import java.util.List;
import java.util.Queue;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Algorithm {

    //private Queue<Move> pastMoves;
    private AtomicLong objFuncVal;
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
        objFuncVal = new AtomicLong(0);
        //pastMoves = new ConcurrentLinkedQueue<>();
    }

    public abstract State run();

    protected double getObjFuncValueByDistrict(District district, Map<Measure, Double> weights) {
        return redistrictedState.getDistrictById(district.getDistrictId()).calculateObjFuncValue(weights);
    }

    public double getObjFuncVal() {
        return Double.longBitsToDouble(objFuncVal.get());
    }

    public void setObjFuncVal(double objFuncVal) {
        this.objFuncVal.set(Double.doubleToLongBits(objFuncVal));
    }

    protected boolean isTerminated() {
        return terminated;
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

    public State getRedistrictedState() {
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

    public void setTerminated(boolean terminated) { this.terminated = terminated; }

//    public void addMove(Move move) {
//        pastMoves.add(move);
//    }
//
//    public Move[] flushPastMoves() {
//        Move[] moves = new Move[0];
//        synchronized (pastMoves) {
//            moves = pastMoves.toArray(moves);
//            pastMoves.clear();
//        }
//        return moves;
//    }
}
