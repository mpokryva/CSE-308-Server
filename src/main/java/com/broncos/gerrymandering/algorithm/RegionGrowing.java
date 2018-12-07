package com.broncos.gerrymandering.algorithm;

import com.broncos.gerrymandering.model.*;
import com.broncos.gerrymandering.util.StateManager;
import org.locationtech.jts.geom.Polygonal;
import org.locationtech.jts.geom.prep.PreparedPolygon;

import java.util.*;


public class RegionGrowing extends Algorithm {

    private SeedPrecinctCriterion criterion;
    private Set<Precinct> unassignedPrecincts;
    private int regions;

    public RegionGrowing(StateCode stateCode, int regions,
                         SeedPrecinctCriterion criterion, Map<Measure, Double> weights) {
        super(stateCode, weights);
        this.criterion = criterion;
        this.unassignedPrecincts = new HashSet<>();
        for (District district: getInitialState().getDistricts()) {
            for(Precinct precinct: district.getPrecincts()) {
                unassignedPrecincts.add(precinct);
            }
        }
        this.regions = regions;
        this.setRedistrictedState(getInitialState().cloneForRG());
        Set<Precinct> seedPrecincts = selectSeedPrecincts(criterion);
        State redistrictedState = this.getRedistrictedState();
        int districtId = 1;
        for (Precinct precinct : seedPrecincts) {
            District initialDistrict = new District(districtId++, redistrictedState, precinct);
            redistrictedState.addDistrict(initialDistrict);
            unassignedPrecincts.remove(precinct);
//            Move move = new Move(precinct, initialDistrict, null, weights);
//            addMove(move);
        }
    }

    @Override
    public State run() {
        int failedMoves = 0;
        while (!unassignedPrecincts.isEmpty()) {
            if(failedMoves > 2000 || isTerminated()) break;
            District district = getRedistrictedState().getRandomDistrict();
            Precinct precinctToMove = nextPrecinctToMove(district);
            if (precinctToMove == null) {
                //TODO: figure out how to deal with holes
                if(unassignedPrecincts.size() < 100 &&
                        (precinctToMove = getEnclosedPrecinct(district)) == null)
                failedMoves++;
                continue;
            }
            unassignedPrecincts.remove(precinctToMove);
            Move move = new Move(precinctToMove, district, null, this.getWeights());
            double prevValue = getRedistrictedState().getObjFuncVal(getWeights());
            move.make();
            //TODO: Play around with randomness of accepting worse moves
            if (prevValue < move.getObjFuncVal() && Math.random() > 0.5) {
                move.revert();
                unassignedPrecincts.add(precinctToMove);
            }else {
                setObjFuncVal(move.getObjFuncVal());
                //addMove(move);
            }
        }
        return getRedistrictedState();
    }

    private Set<Precinct> selectSeedPrecincts(SeedPrecinctCriterion criterion) {
        Set<Precinct> seedPrecincts = new HashSet<>();
        State initialState = getInitialState();
        if (criterion == SeedPrecinctCriterion.RANDOM) {
            for (int i = 0; i < this.regions; i++) {
                District district = initialState.getRandomDistrict();
                seedPrecincts.add(district.getRandomPrecinct());
            }
        } else if (criterion == SeedPrecinctCriterion.INCUMBENT) {
            // TODO: Implement this.
        }
        return seedPrecincts;
    }

    private Precinct nextPrecinctToMove(District district) {
        for (Precinct precinct : district.getBorderPrecincts()) {
            Precinct neighbor = precinct.getRandomNeighbor();
            if (unassignedPrecincts.contains(neighbor)) {
                return neighbor;
            }
        }
        return null;
    }
    //Check if any unassigned precinct are enclosed completely by district
    //TODO: fix this :(
    private Precinct getEnclosedPrecinct(District district) {
        PreparedPolygon prepDistrict = new PreparedPolygon((Polygonal)district.getGeometry());
        for (Precinct precinct : unassignedPrecincts) {
            if(prepDistrict.containsProperly(precinct.getGeometry()))
                return precinct;
        }
        return null;
    }

    public static void main(String[] args) {
        Map<Measure, Double> weights = new HashMap<>();
        weights.put(Measure.EFFICIENCY_GAP, 1.0);
        RegionGrowing rg = new RegionGrowing(StateCode.NM, 3, SeedPrecinctCriterion.RANDOM, weights);
        rg.run();
        System.out.println("hello");
    }
}
