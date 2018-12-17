package com.broncos.gerrymandering.algorithm;

import com.broncos.gerrymandering.model.*;
import com.broncos.gerrymandering.util.MoveSerializer;
import com.broncos.gerrymandering.util.StateManager;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.locationtech.jts.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by kristiancharbonneau on 11/27/18.
 */
@JsonSerialize(using = MoveSerializer.class)
public class Move {
    private Precinct precinct;
    private District destination;
    private District source;
    private double objFuncVal;
    private double preMoveVal;
    private double sourceCompactness;
    private double destinationCompactness;
    private Map<Measure, Double> weights;

    public Move(Precinct precinct, District destination, District source, Map<Measure, Double> weights) {
        this.preMoveVal = destination.getState().getObjFuncVal(weights);
        this.precinct = precinct;
        this.destination = destination;
        this.source = source;
        if (source != null) {
            this.sourceCompactness = this.source.calculateCompactness();
        }
        if (destination != null) {
            this.destinationCompactness = this.destination.calculateCompactness();
        }
        this.weights = weights;
    }

    public void make() {
        if (destination != null) {
            destination.addPrecinct(precinct);
            objFuncVal = destination.getState().getObjFuncVal(weights);
        }
        if (source != null) {
            source.removePrecinct(precinct);
            objFuncVal = source.getState().getObjFuncVal(weights);
        }

    }

    public void revert() {
        District temp = this.source;
        this.source = destination;
        this.destination = temp;
        this.make();
        if (this.preMoveVal != this.objFuncVal) {
            System.out.println("ERROR");
        }
//        if (source != null) {
//            Assert.equals(this.source.calculateCompactness(), destinationCompactness);
//        }
//        if (destination != null) {
//            Assert.equals(this.destination.calculateCompactness(), sourceCompactness);
//        }
        //Assert.equals(this.objFuncVal, this.preMoveVal);
    }

    public double getObjFuncVal() {
        return objFuncVal;
    }

    public Precinct getPrecinct() {
        return precinct;
    }

    public void setPrecinct(Precinct precinct) {
        this.precinct = precinct;
    }

    public District getDestination() {
        return destination;
    }

    public void setDestination(District destination) {
        this.destination = destination;
    }

    public District getSource() {
        return source;
    }

    public void setSource(District source) {
        this.source = source;
    }

    public void setObjFuncVal(double objFuncVal) {
        this.objFuncVal = objFuncVal;
    }

    public Map<Measure, Double> getWeights() {
        return weights;
    }

    public void setWeights(Map<Measure, Double> weights) {
        this.weights = weights;
    }

    public static void main(String[] args) {
        //precincts: 3504918, 3504985, 3504919
        StateManager sm = StateManager.getInstance();
        State nm = sm.getState(StateCode.NM);
        Map<Measure, Double> weights = new HashMap<>();
        weights.put(Measure.EFFICIENCY_GAP, 1.0);
        final int iterations = 1000;
        for (int i = 0; i < iterations; i++) {
            District r = sm.getDistrict(new Random().nextInt(5) + 1, StateCode.CT);
            double refCompactness = r.calculateCompactness();
            Precinct random = r.getRandomPrecinct();
            r.removePrecinct(random);
            r.addPrecinct(random);
            Assert.equals(r.calculateCompactness(), refCompactness);
            System.out.println("Good: " + i);
        }
//        Precinct p1 = sm.getPrecinct(3504918, 3, StateCode.NM);
//        Precinct p2 = sm.getPrecinct(3504985, 3, StateCode.NM);
//        Precinct p3 = sm.getPrecinct(3504919, 3, StateCode.NM);
//        Set<Precinct> borders = dest.getBorderPrecincts();
//        Move m = new Move(p1, dest, null, weights);
//        m.make();
//        System.out.println("OBJ FUNC: " + m.objFuncVal);
//        m = new Move(p2, dest, null, weights);
//        m.make();
//        System.out.println("OBJ FUNC: " + m.objFuncVal);
//        m = new Move(p3, dest, null, weights);
//        m.make();
//        System.out.println("OBJ FUNC: " + m.objFuncVal);
    }
}
