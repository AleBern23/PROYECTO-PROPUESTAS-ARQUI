/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example;

import java.util.logging.Level;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author UUSARIO
 */
public class PersistenceManager {

    private static final boolean DEBUG = true;
    private static final PersistenceManager singleton = new PersistenceManager();
    protected EntityManagerFactory emf;

    public static PersistenceManager getInstance() {
        return singleton;
    }

    public PersistenceManager() {
    }

    public EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            createEntityManagerFactory();
        }
        return emf;
    }

    private void createEntityManagerFactory() {
        this.emf = Persistence.createEntityManagerFactory("CompetitorsPU", System.getProperties());
        if (DEBUG) {
            System.out.println("Persistencia iniciada en " + new java.util.Date());
        }
    }

    public void closeEntityManagerFactory() {
        if (emf != null) {
            emf.close();
            emf = null;
            if (DEBUG) {
                System.out.println("Persistencia finalizada en " + new java.util.Date());
            }
        }
    }
}
