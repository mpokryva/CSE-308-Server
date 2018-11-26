package com.broncos.gerrymandering.spring;

import com.broncos.gerrymandering.model.StateCode;
import com.broncos.gerrymandering.util.StateManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.broncos.gerrymandering.model"})
public class GerrymanderingApplication {

    public static void main(String[] args) {
        SpringApplication.run(GerrymanderingApplication.class, args);
//        init();
    }

    private static void init() {
        StateManager sm = StateManager.getInstance();
        long start = System.currentTimeMillis();
        for (StateCode stateCode: StateCode.values()) {
            sm.getState(stateCode);
        }
        System.out.println("Time taken: " + Long.toString(System.currentTimeMillis() - start) + " ms");
    }
}
