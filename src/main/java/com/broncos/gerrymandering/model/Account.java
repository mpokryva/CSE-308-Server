package com.broncos.gerrymandering.model;

import com.broncos.gerrymandering.util.DefaultEntityManagerFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.persistence.*;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;

/**
 * Created by kristiancharbonneau on 11/25/18.
 */
@Entity(name = "ACCOUNT")
public class Account implements Serializable{
    @Id
    @GeneratedValue
    private int id;
    @Column(name = "IS_ADMIN")
    @JsonIgnore
    private boolean isAdmin;
    @Column(name = "EMAIL", unique=true)
    private String email;
    @Column(name = "PASSWORD")
    private UUID password;
    @Column(name = "USERNAME", unique=true)
    private String username;
    @Column(name = "EFFICIENCY_GAP")
    private Double efficiencyGap;
    @Column(name = "COMPACTNESS")
    private Double compactness;
    @Column(name = "PARTISAN_FAIRNESS")
    private Double partisanFairness;
    @Column(name = "POPULATION_EQUALITY")
    private Double populationEquality;

    public Account() {
    }

    public Account(String email, String password, String username) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        this.isAdmin = false;
        this.email = email;
        this.password = UUID.nameUUIDFromBytes(md.digest());
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAdmin() { return isAdmin; }

    public String getEmail() { return email; }

    public static Account getByUsername(String username) {
        EntityManager em = DefaultEntityManagerFactory.getInstance().createEntityManager();
        final String qText = "SELECT a FROM ACCOUNT a WHERE a.username = :username";
        Query query = em.createQuery(qText);
        query.setParameter("username", username);
        List<Account> results = query.getResultList();
        Account account = null;
        if (results != null && results.size() > 0) {
            account = results.get(0);
        }
        em.close();
        return account;
    }

    public boolean checkPassword(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            UUID hashedPass = UUID.nameUUIDFromBytes(md.digest());
            return (hashedPass.compareTo(this.password) == 0);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void update(String email, String username) {
        this.email = email;
        this.username = username;
    }

    public void setWeights(Double efficiencyGap, Double partisanFairness,
                           Double compactness, Double populationEquality) {
        this.efficiencyGap = efficiencyGap;
        this.partisanFairness = partisanFairness;
        this.compactness = compactness;
        this.populationEquality = populationEquality;
    }

    public Map<String, Double> getWeights() {
        return new HashMap<String, Double>(){{
            put("efficiencyGap", efficiencyGap);
            put("partisanFairness", partisanFairness);
            put("compactness", compactness);
            put("populationEquality", populationEquality);
        }};
    }

    @Override
    public String toString() {
        return "Account{" +
                "isAdmin=" + isAdmin +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                '}';
    }


    public static void main(String[] args) {
        EntityManager em = DefaultEntityManagerFactory.getEntityManager();
        em.getTransaction().begin();
        Account a = getByUsername("test2");
        System.out.println(a.checkPassword("1234"));
        System.out.println(a.checkPassword("123423"));
        em.getTransaction().commit();
        em.close();
    }
}
