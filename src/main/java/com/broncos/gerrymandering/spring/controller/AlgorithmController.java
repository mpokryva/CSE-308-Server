package com.broncos.gerrymandering.spring.controller;

import com.broncos.gerrymandering.algorithm.Algorithm;
import com.broncos.gerrymandering.algorithm.Move;
import com.broncos.gerrymandering.algorithm.RegionGrowing;
import com.broncos.gerrymandering.algorithm.SeedPrecinctCriterion;
import com.broncos.gerrymandering.spring.dto.AlgorithmDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


/**
 * Created by kristiancharbonneau on 11/29/18.
 */
@CrossOrigin(origins = "http://localhost:9000")
@RestController
public class AlgorithmController {
    private static final String REGION_GROWING = "RegionGrowing";
    private static final String SIMULATED_ANNEALING = "SimulatedAnnealing";

    private Algorithm algorithm;

    @RequestMapping(value = "/algorithm/new",
            method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public String register(@RequestBody AlgorithmDTO algorithmDTO) {
        if(algorithmDTO.getType().equals(REGION_GROWING)) {
            SeedPrecinctCriterion criterion = SeedPrecinctCriterion.valueOf(algorithmDTO.getVariation());
            algorithm = new RegionGrowing(algorithmDTO.getStateCode(), algorithmDTO.getRegions(),
                        criterion, algorithmDTO.getWeights());
        }
        System.out.println(algorithmDTO.getRegions());
        algorithm.run();
        return "DONE (for testing)";
    }

    @RequestMapping(value = "/algorithm/get-update",
            method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public Move[] register(String username) {
        if(algorithm != null)
            return algorithm.flushPastMoves();
        else
            return new Move[0];
    }
}
