package com.broncos.gerrymandering.model;

import com.broncos.gerrymandering.util.DefaultEntityManager;

import java.security.MessageDigest;
import java.util.List;
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
    private boolean isAdmin;
    @Column(name = "EMAIL", unique=true)
    private String email;
    @Column(name = "PASSWORD")
    private UUID password;
    @Column(name = "USERNAME", unique=true)
    private String username;

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

    public static Account getByUsername(String username) {
        EntityManager em = DefaultEntityManager.getDefaultEntityManager();
        final String qText = "SELECT a FROM ACCOUNT a WHERE a.username = :username";
        Query query = em.createQuery(qText);
        query.setParameter("username", username);
        List<Account> results = query.getResultList();
        Account account = null;
        if (results != null && results.size() > 0) {
            account = results.get(0);
        }
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

    @Override
    public String toString() {
        return "Account{" +
                "isAdmin=" + isAdmin +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    public static void main(String[] args) {
        EntityManager em = DefaultEntityManager.getDefaultEntityManager();
        em.getTransaction().begin();
        Account a = getByUsername("test2");
        System.out.println(a.checkPassword("1234"));
        System.out.println(a.checkPassword("123423"));
        em.getTransaction().commit();
    }
}
