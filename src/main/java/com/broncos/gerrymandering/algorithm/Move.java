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

    public void revert() {

    }

    public static void main(String[] args) {
        StateManager sm = StateManager.getInstance();
        //74496        73568
        //district id = 213
        State nm = sm.getState(StateCode.NM);
        Map <Measure, Double> weights = new HashMap<>();
        weights.put(Measure.EFFICIENCY_GAP, 1.0);
        System.out.println("OBJ FUNC: " + nm.getObjFuncVal(weights));
        District dest = sm.getDistrict(3, StateCode.NM);
        Precinct p = sm.getPrecinct(350575, 1, StateCode.NM);
        Move m = new Move(p, dest, null, weights);
        m.make();
        System.out.println("OBJ FUNC: " + m.objFuncVal);
//        Set<Precinct> borders = dest.getBorderPrecincts();
//        for(Precinct p: borders) {
//            System.out.println(p.getPrecinctId());
//        }
    }
}
