package com.broncos.gerrymandering.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Created by mpokr on 11/24/2018.
 */

// TODO: Inject the em.
public class DefaultEntityManager {

    // TODO: Move to application.properties
    private static final String dbName = "broncos";

    public static EntityManager getDefaultEntityManager() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(dbName);
        return emf.createEntityManager();
    }

}
