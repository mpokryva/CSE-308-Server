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
    }

    public void revert() {

    }

    public static void main(String[] args) {
        StateManager sm = StateManager.getInstance();
        //74496        73568
        //district id = 213
        District dest = sm.getDistrict(213, StateCode.NM);
        Precinct p = sm.getPrecinct(73568, 211, StateCode.NM);
        System.out.println(dest);
        Move m = new Move(p, dest, null);
        m.make();

    }
}
