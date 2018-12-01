package com.broncos.gerrymandering.util;

import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Created by mpokr on 11/24/2018.
 */

@Component
public class DefaultEntityManagerFactory {


    private final static String DB_NAME = "broncos";
    private static EntityManagerFactory emf;
    private static EntityManager em;

    public static EntityManagerFactory getInstance() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory(DB_NAME);
        }
        return emf;
    }

    public static EntityManager getEntityManager() {
        if (em == null) {
            em = getInstance().createEntityManager();
        }
        return em;
    }

    public static void close() {
        emf.close();
    }

}
