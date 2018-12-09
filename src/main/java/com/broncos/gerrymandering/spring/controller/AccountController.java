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
@CrossOrigin(origins = "http://localhost:9000")
@RestController
public class AccountController {

    @Value("${account.username}")
    private String USERNAME_KEY;
    @Value("${account.password}")
    private String PASSWORD_KEY;
    @Value("${account.email}")
    private String EMAIL_KEY;
    @Value("${zero}")
    private int ZERO;


    @RequestMapping(value = "/register",
            method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity register(@RequestBody Map<String, String> payload, HttpServletResponse resp) {
        EntityManager em = DefaultEntityManagerFactory.getEntityManager();
        em.getTransaction().begin();
        Account account = new Account(payload.get(EMAIL_KEY), payload.get(PASSWORD_KEY), payload.get(USERNAME_KEY));
        try {
            em.persist(account);
            em.getTransaction().commit();
            resp.addCookie(new Cookie(USERNAME_KEY, account.getUsername()));
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/login",
            method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity login(@RequestBody Map<String, String> payload, HttpServletResponse resp) {
        Account account = Account.getByUsername(payload.get(USERNAME_KEY));
        if (account != null && account.checkPassword(payload.get(PASSWORD_KEY))) {
            resp.addCookie(new Cookie(USERNAME_KEY, account.getUsername()));
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
        String username = payload.get(USERNAME_KEY);
        Cookie[] cookies = req.getCookies();
        for(Cookie cookie: cookies) {
            if(cookie.getName().equals(USERNAME_KEY) && cookie.getValue().equals(username)) {
                Cookie newCookie = new Cookie(USERNAME_KEY, null);
                newCookie.setMaxAge(ZERO);
                resp.addCookie(newCookie);
                return new ResponseEntity(HttpStatus.OK);
            }
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }



}
