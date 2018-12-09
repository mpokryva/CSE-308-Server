package com.broncos.gerrymandering.spring.controller;

import com.broncos.gerrymandering.algorithm.Algorithm;
import com.broncos.gerrymandering.algorithm.AlgorithmManager;
import com.broncos.gerrymandering.model.District;
import com.broncos.gerrymandering.model.Precinct;
import com.broncos.gerrymandering.model.State;
import com.broncos.gerrymandering.spring.dto.SaveMapDTO;
import com.broncos.gerrymandering.util.DefaultEntityManagerFactory;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:9000")
@RestController
@RequestMapping("/map")
public class MapManagementController {

    @PostMapping(
            value = "/save",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity saveMap(@RequestBody SaveMapDTO saveMapDTO) {
        UUID sessionUUID = UUID.fromString(saveMapDTO.getSessionId());
        Algorithm algorithm = AlgorithmManager.getInstance().getAlgorithm(sessionUUID);
        State state = algorithm.getRedistrictedState();
        EntityManager em = DefaultEntityManagerFactory.getEntityManager();
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        em.getTransaction().begin();
        if (state.isOriginal()) {
            throw new IllegalStateException("State cannot be an original state");
        }
        for (District district : state.getDistricts()) {
            if (district.getState() != state) {
                System.out.println("District state not equal!!");
            } else {
                for (Precinct precinct : district.getPrecincts()) {
                    if (precinct.getState() != state) {
                        System.out.println("Precinct state not equal!!");
                    }
                    if (precinct.getDistrict() != district) {
                        System.out.println("Precinct district not equal!!");
                    }
                }
            }
        }
        em.persist(state);
        em.getTransaction().commit();
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
