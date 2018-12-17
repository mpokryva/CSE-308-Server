package com.broncos.gerrymandering.algorithm;

import com.broncos.gerrymandering.model.*;
import com.broncos.gerrymandering.util.StateManager;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Algorithm {

    //private Queue<Move> pastMoves;
    private AtomicLong objFuncVal;
    private Map<Measure, Double> weights;
    private Set<Integer> excludedDistricts;
    private boolean terminated;
    private StateCode stateCode;
    private State redistrictedState;
    private State initialState;

    public Algorithm(StateCode stateCode, Map<Measure, Double> weights, Set<Integer> excludedDistricts) {
        this.stateCode = stateCode;
        this.initialState = StateManager.getInstance().getState(stateCode);
        this.excludedDistricts = excludedDistricts;
        this.weights = weights;
        objFuncVal = new AtomicLong(0);
        //pastMoves = new ConcurrentLinkedQueue<>();
    }

    public abstract State run();

    public Set<Integer> getExcludedDistricts() {
        if (excludedDistricts == null) {
            excludedDistricts = new HashSet<>();
        }
        return excludedDistricts;
    }

    public double getObjFuncVal() {
        return Double.longBitsToDouble(objFuncVal.get());
    }

    public void setObjFuncVal(double objFuncVal) {
        this.objFuncVal.set(Double.doubleToLongBits(objFuncVal));
    }

    public boolean isTerminated() {
        return terminated;
    }

    protected Map<Measure, Double> getWeights() {
        return weights;
    }

    public State getRedistrictedState() {
        return redistrictedState;
    }

    protected State getInitialState() {
        return initialState;
    }

    protected void setRedistrictedState(State redistrictedState) {
        this.redistrictedState = redistrictedState;
    }

    public void setTerminated(boolean terminated) {
        this.terminated = terminated;
    }

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
