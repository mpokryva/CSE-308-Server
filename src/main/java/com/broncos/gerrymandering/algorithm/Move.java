package com.broncos.gerrymandering.algorithm;

import com.broncos.gerrymandering.model.District;
import com.broncos.gerrymandering.model.Precinct;
import com.broncos.gerrymandering.model.Measure;
import com.broncos.gerrymandering.model.StateCode;
import com.broncos.gerrymandering.util.StateManager;
import org.locationtech.jts.geom.Geometry;

import java.util.Map;

/**
 * Created by kristiancharbonneau on 11/27/18.
 */
public class Move {
    private Precinct precinct;
    private District destination;
    private District source;
    private double objFuncVal;
    private Map<Measure, Double> weights;

    public Move(Precinct precinct, District destination, District source) {
        this.precinct = precinct;
        this.destination = destination;
        this.source = source;
    }

    public void make() {
        destination.addPrecinct(precinct);
        objFuncVal = destination.getState().getObjFuncVal();
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
        District dest = sm.getDistrict(3, StateCode.NM);
        Precinct p = sm.getPrecinct(350575, 1, StateCode.NM);
        System.out.println(dest.getBoundary());
        Move m = new Move(p, dest, null);
        m.make();
        System.out.println(dest.getBoundary());
    }
}
