package com.broncos.gerrymandering.algorithm;

import com.broncos.gerrymandering.model.*;
import com.broncos.gerrymandering.util.StateManager;
import org.locationtech.jts.geom.Geometry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by kristiancharbonneau on 11/27/18.
 */
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

    public double getObjFuncVal() {
        return objFuncVal;
    }

    public void revert() {

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
