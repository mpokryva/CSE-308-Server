package com.broncos.gerrymandering.spring.controller;

import com.broncos.gerrymandering.algorithm.Algorithm;
import com.broncos.gerrymandering.algorithm.RegionGrowing;
import com.broncos.gerrymandering.algorithm.SeedPrecinctCriterion;
import com.broncos.gerrymandering.model.State;
import com.broncos.gerrymandering.spring.dto.AlgorithmDTO;
import com.broncos.gerrymandering.spring.dto.AlgorithmUpdateDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity newAlgorithm(@RequestBody AlgorithmDTO algorithmDTO) {
        if(algorithmDTO.getType().equals(REGION_GROWING)) {
            SeedPrecinctCriterion criterion = SeedPrecinctCriterion.valueOf(algorithmDTO.getVariation());
            algorithm = new RegionGrowing(algorithmDTO.getStateCode(), algorithmDTO.getExcludedDistricts(),
                       algorithmDTO.getSeedIds(), criterion, algorithmDTO.getWeights(), algorithmDTO.getRegions());
        }
        System.out.println(algorithmDTO.getRegions());
        algorithm.run();
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/algorithm/stop",
            method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity stopAlgorithm(@RequestBody String username) {
        if(algorithm == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }else {
            algorithm.setTerminated(true);
            return new ResponseEntity(HttpStatus.OK);
        }

    }


    @RequestMapping(value = "/algorithm/get-update",
            method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public AlgorithmUpdateDTO register(String username) {
        if(algorithm != null) {
//            return algorithm.flushPastMoves();
            State redistrictedState = algorithm.getRedistrictedState();
            return new AlgorithmUpdateDTO(redistrictedState.getDistricts(),
                    algorithm.getObjFuncVal());
        } else {
            return new AlgorithmUpdateDTO();
        }
    }
}
