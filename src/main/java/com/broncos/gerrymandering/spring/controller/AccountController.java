package com.broncos.gerrymandering.spring.controller;

import com.broncos.gerrymandering.model.Account;
import com.broncos.gerrymandering.util.DefaultEntityManager;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import java.util.Map;

/**
 * Created by kristiancharbonneau on 11/25/18.
 */
@RestController
public class AccountController {

    @RequestMapping(value = "/register",
            method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity register(@RequestBody Map<String, String> payload) {
        EntityManager em = DefaultEntityManager.getDefaultEntityManager();
        em.getTransaction().begin();
        Account account = new Account(payload.get("email"), payload.get("password"), payload.get("username"));
        try {
            em.persist(account);
            em.getTransaction().commit();
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/login",
            method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity login(@RequestBody Map<String, String> payload) {
        Account account = Account.getByUsername(payload.get("username"));
        if (account != null && account.checkPassword(payload.get("password"))) {
            return new ResponseEntity(HttpStatus.OK);
        }else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }



}
