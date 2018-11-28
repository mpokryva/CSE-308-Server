package com.broncos.gerrymandering.spring.controller;

import com.broncos.gerrymandering.model.Account;
import com.broncos.gerrymandering.util.DefaultEntityManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by kristiancharbonneau on 11/25/18.
 */
@RestController
public class AccountController {

    @Value("${cookie.username}")
    private String username;

    @RequestMapping(value = "/register",
            method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity register(@RequestBody Map<String, String> payload, HttpServletResponse resp) {
        System.out.println(username);
        EntityManager em = DefaultEntityManagerFactory.getInstance().createEntityManager();
        em.getTransaction().begin();
        Account account = new Account(payload.get("email"), payload.get("password"), payload.get("username"));
        try {
            em.persist(account);
            em.getTransaction().commit();
            resp.addCookie(new Cookie("username", account.getUsername()));
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/login",
            method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity login(@RequestBody Map<String, String> payload, HttpServletResponse resp) {
        Account account = Account.getByUsername(payload.get("username"));
        if (account != null && account.checkPassword(payload.get("password"))) {
            resp.addCookie(new Cookie("username", account.getUsername()));
            return new ResponseEntity(HttpStatus.OK);
        }else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/logout",
            method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity logout(@RequestBody Map<String, String> payload,
                                 HttpServletRequest req,
                                 HttpServletResponse resp) {
        String username = payload.get("username");
        Cookie[] cookies = req.getCookies();
        for(Cookie cookie: cookies) {
            if(cookie.getName().equals("username") && cookie.getValue().equals(username)) {
                Cookie newCookie = new Cookie("username", null);
                newCookie.setMaxAge(0);
                resp.addCookie(newCookie);
                return new ResponseEntity(HttpStatus.OK);
            }
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }



}
