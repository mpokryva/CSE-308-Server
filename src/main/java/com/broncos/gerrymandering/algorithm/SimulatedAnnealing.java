package com.broncos.gerrymandering.algorithm;

import com.broncos.gerrymandering.model.*;

import java.util.*;

public class SimulatedAnnealing extends Algorithm {

    private int successiveFailures;
    private DistrictSelectionCriterion criterion;
    private PriorityQueue<District> descObjValDistricts;
    private double temperature;
    private static final double K = -1e-10;
    private static final int MAX_MOVES = 1000;
    private static final int MAX_SUCCESSIVE_FAILURES = 500;

    public SimulatedAnnealing(StateCode stateCode, Set<Integer> excludedDistricts,
                              Map<Measure, Double> weights, DistrictSelectionCriterion criterion) {
        super(stateCode, weights, excludedDistricts);
        setRedistrictedState(getInitialState().cloneForSA());
        this.criterion = criterion;
        descObjValDistricts = new PriorityQueue<>(Collections.reverseOrder(new DistrictComparator(weights)));
        this.getRedistrictedState().getDistricts().forEach(District::calculateCompactness);
        descObjValDistricts.addAll(this.getRedistrictedState().getDistricts());
        temperature = 1;
    }

    @Override
    public State run() {
        int moves = 0;
        while (successiveFailures < MAX_SUCCESSIVE_FAILURES && temperature != 0 && moves < MAX_MOVES) {
            System.out.printf("SA moves #: %d\n", moves);
            District source = nextSourceDistrict();
            District destination = nextDestinationDistrict();
            Precinct precinctToMove = nextPrecinctToMove(source);
            double preMoveVal = this.getRedistrictedState().getObjFuncVal(this.getWeights());
            Move move = new Move(precinctToMove, destination, source, this.getWeights());
            move.make();
            double delta = preMoveVal - move.getObjFuncVal();
            if (delta > 0) {
                double acceptanceProb = Math.exp(delta / (K * temperature));
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
                descObjValDistricts.remove(destination);
                descObjValDistricts.add(source);
                descObjValDistricts.add(destination);
            }
            setObjFuncVal(getRedistrictedState().getObjFuncVal(getWeights()));
            moves++;
            temperature = (MAX_MOVES - moves) / (double) MAX_MOVES;
        }
        setTerminated(true);
        return this.getRedistrictedState();
    }


    private Precinct nextPrecinctToMove(District district) {

        return district.getRandomBorderPrecinct();
    }

    private District nextSourceDistrict() {
        District source = null;
        if (criterion == DistrictSelectionCriterion.RANDOM) {
            source = this.getRedistrictedState().getRandomDistrict();
        } else if (criterion == DistrictSelectionCriterion.LOWEST_OBJ_VAL) {
            source = descObjValDistricts.peek();
        }
        return source;
    }

    private District nextDestinationDistrict() {
        District destination = null;
        if (criterion == DistrictSelectionCriterion.RANDOM) {
            destination = this.getRedistrictedState().getRandomDistrict();
        } else if (criterion == DistrictSelectionCriterion.LOWEST_OBJ_VAL) {
            District temp = descObjValDistricts.poll();
            // Get second lowest district.
            destination = descObjValDistricts.peek();
            descObjValDistricts.add(temp);
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
            double diff = o1.calculateCompactness() - o2.calculateCompactness();
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
