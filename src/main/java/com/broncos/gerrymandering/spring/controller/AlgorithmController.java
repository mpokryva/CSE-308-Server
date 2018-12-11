package com.broncos.gerrymandering.spring.controller;

import com.broncos.gerrymandering.algorithm.Algorithm;
import com.broncos.gerrymandering.algorithm.AlgorithmManager;
import com.broncos.gerrymandering.algorithm.RegionGrowing;
import com.broncos.gerrymandering.algorithm.SeedPrecinctCriterion;
import com.broncos.gerrymandering.model.State;
import com.broncos.gerrymandering.spring.dto.AlgorithmDTO;
import com.broncos.gerrymandering.spring.dto.AlgorithmUpdateDTO;
import com.broncos.gerrymandering.util.NewAlgorithmResponse;
import com.broncos.gerrymandering.spring.dto.SessionIdDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

/**
 * Created by kristiancharbonneau on 11/29/18.
 */
@CrossOrigin(origins = "http://localhost:9000")
@RestController
@RequestMapping("/algorithm")
public class AlgorithmController {
    private static final String REGION_GROWING = "RegionGrowing";
    private static final String SIMULATED_ANNEALING = "SimulatedAnnealing";

    @PostMapping(value = "/new",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public NewAlgorithmResponse newAlgorithm(@Valid @RequestBody AlgorithmDTO algorithmDTO) {
        System.out.println(algorithmDTO.getRegions());
        if (algorithmDTO.getExcludedDistricts() != null &&
                algorithmDTO.getRegions() < algorithmDTO.getExcludedDistricts().size()) {
            throw new IllegalArgumentException("Number of regions cannot be less than number of " +
                    "excluded districts.");
        }
        UUID sessionId = UUID.randomUUID();
        Runnable runnable = () -> {
            Algorithm algorithm;
            if (algorithmDTO.getType().equals(REGION_GROWING)) {
                SeedPrecinctCriterion criterion = SeedPrecinctCriterion.valueOf(algorithmDTO.getVariation());
                algorithm = new RegionGrowing(algorithmDTO.getStateCode(), algorithmDTO.getExcludedDistricts(),
                        algorithmDTO.getSeedIds(), criterion, algorithmDTO.getWeights(), algorithmDTO.getRegions());
            } else {
                algorithm = null;
            }
            AlgorithmManager.getInstance().addAlgorithm(sessionId, algorithm);
            algorithm.run();
        };
        Thread t = new Thread(runnable);
        t.start();
        return new NewAlgorithmResponse(sessionId);
    }

    @PostMapping(value = "/stop")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity stopAlgorithm(@RequestBody SessionIdDTO sessionIdDTO) {
        UUID sessionUUID = UUID.fromString(sessionIdDTO.getSessionId());
        Algorithm algorithm = AlgorithmManager.getInstance().getAlgorithm(sessionUUID);
        if (algorithm == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            algorithm.setTerminated(true);
            return new ResponseEntity(HttpStatus.OK);
        }

    }


    @RequestMapping(value = "/get-update",
            method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity getUpdate(@RequestParam String sessionId) {
        UUID sessionUUID = UUID.fromString(sessionId);
        Algorithm algorithm = AlgorithmManager.getInstance().getAlgorithm(sessionUUID);
        if (algorithm != null) {
//            return algorithm.flushPastMoves();
            State redistrictedState = algorithm.getRedistrictedState();
            return ResponseEntity.status(HttpStatus.OK).body(new AlgorithmUpdateDTO(redistrictedState.getDistricts(),
                    algorithm.getObjFuncVal(), algorithm.isTerminated()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
