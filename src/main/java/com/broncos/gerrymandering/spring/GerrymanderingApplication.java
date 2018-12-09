package com.broncos.gerrymandering.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.EntityManager;

@SpringBootApplication
@EntityScan({"com.broncos.gerrymandering.model"})
public class GerrymanderingApplication {

    public static void main(String[] args) {
        SpringApplication.run(GerrymanderingApplication.class, args);
    }

}
