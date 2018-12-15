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

    public RegionGrowing(StateCode stateCode, Set<Integer> excludedDistricts, Set<Integer> seedIds,
                         SeedPrecinctCriterion criterion, Map<Measure, Double> weights, int regions) {
        super(stateCode, weights, excludedDistricts);
        this.criterion = criterion;
        this.unassignedPrecincts = new HashSet<>();
        this.regions = regions;
        this.setRedistrictedState(getInitialState().cloneForRG());
        for (District district : getInitialState().getDistricts()) {
            if (this.getExcludedDistricts().contains(district.getDistrictId())) {
                District excDistrict = new District(district.getDistrictId(), getRedistrictedState(), false);
                for (Precinct precinct : district.getPrecincts()) {
                    excDistrict.addPrecinct(precinct);
                }
                getRedistrictedState().addDistrict(excDistrict);
            } else {
                for (Precinct precinct : district.getPrecincts()) {
                    unassignedPrecincts.add(precinct);
                }
            }
        }
        Set<Precinct> seedPrecincts = selectSeedPrecincts(criterion, seedIds);
        int districtId = 1;
        for (Precinct precinct : seedPrecincts) {
            while(getExcludedDistricts().contains(districtId))
                districtId++;
            District initialDistrict = new District(districtId, getRedistrictedState(), false);
            initialDistrict.addPrecinct(precinct);
            getRedistrictedState().addDistrict(initialDistrict);
            unassignedPrecincts.remove(precinct);
            districtId++;
//            Move move = new Move(precinct, initialDistrict, null, weights);
//            addMove(move);
        }
    }

    @Override
    public State run() {
        int failedMoves = 0;
        while (!unassignedPrecincts.isEmpty()) {
            if (failedMoves > 2000 || isTerminated()) break;
            District district = getRedistrictedState().getRandomDistrict();
            if (this.getExcludedDistricts().contains(district.getDistrictId())) continue;
            Precinct precinctToMove = nextPrecinctToMove(district);
            if (precinctToMove == null) {
                failedMoves++;
                continue;
            }
            unassignedPrecincts.remove(precinctToMove);
            System.out.println(unassignedPrecincts.size() + " unassigned precincts left.");
            Move move = new Move(precinctToMove, district, null, this.getWeights());
            double prevValue = getRedistrictedState().getObjFuncVal(getWeights());
            move.make();
            //TODO: Play around with randomness of accepting worse moves
            if (prevValue < move.getObjFuncVal() && Math.random() > 0.5) {
                move.revert();
                unassignedPrecincts.add(precinctToMove);
            } else {
                setObjFuncVal(move.getObjFuncVal());
                //addMove(move);
            }
        }
        if(!isTerminated()) {
            fixHoles();
            setTerminated(true);
        }
        return getRedistrictedState();
    }

    private Set<Precinct> selectSeedPrecincts(SeedPrecinctCriterion criterion, Set<Integer> seedIds) {
        Set<Precinct> seedPrecincts = new HashSet<>();
        State initialState = getInitialState();
        if (criterion == SeedPrecinctCriterion.RANDOM) {
            for (int i = 0; i < (this.regions - getExcludedDistricts().size()) ; i++) {
                District district = initialState.getRandomDistrict();
                while (this.getExcludedDistricts().contains(district.getDistrictId())) {
                    district = initialState.getRandomDistrict();
                }
                seedPrecincts.add(district.getRandomPrecinct());
            }
        } else if (criterion == SeedPrecinctCriterion.INCUMBENT) {
            for (District district : initialState.getDistricts()) {
                for (Integer id : seedIds) {
                    Precinct seed = district.getPrecinctById(id);
                    if (seed != null) {
                        seedPrecincts.add(seed);
                    }
                }
            }
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

    private void fixHoles() {
        for (Precinct precinct : unassignedPrecincts) {
            Precinct neighbor = precinct.getRandomNeighbor();
            while (neighbor.getDistrict() == null)
                neighbor = precinct.getRandomNeighbor();
            District district = neighbor.getDistrict();
            district.addPrecinct(precinct);
        }
    }


}
