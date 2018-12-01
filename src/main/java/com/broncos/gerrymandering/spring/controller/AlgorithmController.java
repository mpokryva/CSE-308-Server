package com.broncos.gerrymandering.spring.controller;

import com.broncos.gerrymandering.model.StateCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by kristiancharbonneau on 11/29/18.
 */
@CrossOrigin(origins = "http://localhost:9000")
@RestController
public class AlgorithmController {

    @RequestMapping(value = "/algorithm/new",
            method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void register(@RequestBody Map<String, String> payload, HttpServletResponse resp) {
        StateCode stateCode = StateCode.valueOf(payload.get("stateCode"));
    }
}
