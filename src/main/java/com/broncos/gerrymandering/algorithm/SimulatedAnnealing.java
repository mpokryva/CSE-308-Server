package com.broncos.gerrymandering.algorithm;

import com.broncos.gerrymandering.model.*;

import java.util.*;

public class SimulatedAnnealing extends Algorithm {

    private final int maxSuccessiveFailures;
    private int successiveFailures;
    private DistrictSelectionCriterion criterion;
    private PriorityQueue<District> descObjValDistricts;
    private List<District> unitialiazedDistricts;
    private double temperature;
    private static final double K = 10;
    private static final int MAX_MOVES = 10000;

    public SimulatedAnnealing(StateCode stateCode, Set<Integer> excludedDistricts,
                              Map<Measure, Double> weights, DistrictSelectionCriterion criterion,
                              int maxSuccessiveFailures) {
        super(stateCode, weights, excludedDistricts);
        setRedistrictedState(getInitialState().cloneForSA());
        this.criterion = criterion;
        this.maxSuccessiveFailures = maxSuccessiveFailures;
        descObjValDistricts = new PriorityQueue<>(Collections.reverseOrder(new DistrictComparator(weights)));
        descObjValDistricts.addAll(this.getRedistrictedState().getDistricts());
        unitialiazedDistricts = new ArrayList<>(this.getRedistrictedState().getDistricts());
        temperature = 1;
    }

    @Override
    public State run() {
        int moves = 0;
        while (successiveFailures < maxSuccessiveFailures) {
            District source = nextSourceDistrict();
            District destination = nextDestinationDistrict();
            Precinct precinctToMove = nextPrecinctToMove(source);
            double preMoveVal = this.getRedistrictedState().getObjFuncVal(this.getWeights());
            Move move = new Move(precinctToMove, destination, source, this.getWeights());
            move.make();
            double delta = move.getObjFuncVal() - preMoveVal;
            if (delta < 0) {
                double acceptanceProb = 1 - Math.exp(delta / (K * temperature));
                if (new Random().nextDouble() > acceptanceProb) {
                    move.revert();
                    successiveFailures++;
                } else {
                    successiveFailures = 0;
                }
            } else {
                successiveFailures = 0;
            }
            if (successiveFailures == 0) {
                descObjValDistricts.remove(source);
                descObjValDistricts.remove(source);
                descObjValDistricts.add(source);
                descObjValDistricts.add(destination);
            }
            moves++;
            temperature = moves / (double) MAX_MOVES;
        }
        setTerminated(true);
        return this.getRedistrictedState();
    }


    private Precinct nextPrecinctToMove(District district) {
        Precinct borderPrecinct = district.getRandomBorderPrecinct();
        return borderPrecinct.getRandomNeighbor();
    }

    private District nextSourceDistrict() {
        District source = null;
        if (criterion == DistrictSelectionCriterion.RANDOM) {
            source =  this.getRedistrictedState().getRandomDistrict();
        } else if (criterion == DistrictSelectionCriterion.LOWEST_OBJ_VAL){
            source = descObjValDistricts.peek();
            if (source == null || source.calculateObjFuncValue(this.getWeights()) == 0) {
                // Pick random district with 0 value, to prevent the same one being picked repeatedly.
                source = unitialiazedDistricts.get(new Random().nextInt(unitialiazedDistricts.size()));
            }
        }
        return source;
    }

    private District nextDestinationDistrict() {
        District destination = null;
        if (criterion == DistrictSelectionCriterion.RANDOM) {
            destination =  this.getRedistrictedState().getRandomDistrict();
        } else if (criterion == DistrictSelectionCriterion.LOWEST_OBJ_VAL){
            District temp = descObjValDistricts.poll();
            // Get second lowest district.
            destination = (!descObjValDistricts.isEmpty()) ? descObjValDistricts.peek() : null;
            descObjValDistricts.add(temp);
            if (destination == null || destination.calculateObjFuncValue(this.getWeights()) == 0) {
                // Pick random district with 0 value, to prevent the same one being picked repeatedly.
                destination = unitialiazedDistricts.get(new Random().nextInt(unitialiazedDistricts.size()));
            }
        }
        return destination;
    }

    private class DistrictComparator implements Comparator<District> {

        private Map<Measure, Double> weights;

        private DistrictComparator(Map<Measure, Double> weights) {
            this.weights = weights;
        }

        @Override
        public int compare(District o1, District o2) {
            double diff = o1.calculateObjFuncValue(weights) - o2.calculateObjFuncValue(weights);
            if (diff > 0) {
                return 1;
            } else if (diff < 0) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
