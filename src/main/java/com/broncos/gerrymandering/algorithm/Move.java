package com.broncos.gerrymandering.algorithm;

import com.broncos.gerrymandering.model.*;
import com.broncos.gerrymandering.util.MoveSerializer;
import com.broncos.gerrymandering.util.StateManager;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.locationtech.jts.geom.Geometry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by kristiancharbonneau on 11/27/18.
 */
@JsonSerialize(using = MoveSerializer.class)
public class Move {
    private Precinct precinct;
    private District destination;
    private District source;
    private double objFuncVal;
    private Map<Measure, Double> weights;

    public Move(Precinct precinct, District destination, District source, Map <Measure, Double> weights) {
        this.precinct = precinct;
        this.destination = destination;
        this.source = source;
        this.weights = weights;
    }

    public void make() {
        destination.addPrecinct(precinct);
        objFuncVal = destination.getState().getObjFuncVal(weights);
        if(source != null) {
            //TODO: for simulated annealing
        }
    }

    public void revert() {

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
        Map <Measure, Double> weights = new HashMap<>();
        weights.put(Measure.EFFICIENCY_GAP, 1.0);
        District dest = sm.getDistrict(1, StateCode.NM);
        Precinct p1 = sm.getPrecinct(3504918, 3, StateCode.NM);
        Precinct p2 = sm.getPrecinct(3504985, 3, StateCode.NM);
        Precinct p3 = sm.getPrecinct(3504919, 3, StateCode.NM);
        Set<Precinct> borders = dest.getBorderPrecincts();
        Move m = new Move(p1, dest, null, weights);
        m.make();
        System.out.println("OBJ FUNC: " + m.objFuncVal);
        m = new Move(p2, dest, null, weights);
        m.make();
        System.out.println("OBJ FUNC: " + m.objFuncVal);
        m = new Move(p3, dest, null, weights);
        m.make();
        System.out.println("OBJ FUNC: " + m.objFuncVal);
    }
}
