package com.broncos.gerrymandering.algorithm;

import com.broncos.gerrymandering.model.*;
import com.broncos.gerrymandering.util.StateManager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class RegionGrowing extends Algorithm {

    private SeedPrecinctCriterion criterion;
    private Set<Precinct> unassignedPrecincts;
    private int regions;

    public RegionGrowing(StateCode stateCode, int regions, SeedPrecinctCriterion criterion) {
        this.setInitialState(StateManager.getInstance().getState(stateCode));
        this.unassignedPrecincts = new HashSet<>();
        Iterator<District> districtIt = getInitialState().districtIterator();
        while (districtIt.hasNext()) {
            District district = districtIt.next();
            Iterator<Precinct> precinctIt = district.precinctIterator();
            while (precinctIt.hasNext()) {
                unassignedPrecincts.add(precinctIt.next());
            }
        }
        this.regions = regions;
        this.setRedistrictedState(getInitialState().clone());
        Set<Precinct> seedPrecincts = selectSeedPrecincts(criterion);
        State redistrictedState = this.getRedistrictedState();
        for (Precinct precinct : seedPrecincts) {
            District initialDistrict = new District();
            initialDistrict.setState(redistrictedState);
            initialDistrict.addPrecinct(precinct);
            redistrictedState.addDistrict(initialDistrict);
            unassignedPrecincts.remove(precinct);
        }
    }

    @Override
    public State run() {
        while (!unassignedPrecincts.isEmpty()) {
            District district = getRedistrictedState().getRandomDistrict();
            Precinct precinctToMove = nextPrecinctToMove(district);
            if (precinctToMove == null) {
                continue;
            }
            Move move = new Move(precinctToMove, district, null, this.getWeights());
            double prevValue = this.getObjFuncValueByDistrict(district, getWeights());
            move.make();
            if (prevValue < this.getObjFuncValueByDistrict(district, getWeights())) {

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
}
