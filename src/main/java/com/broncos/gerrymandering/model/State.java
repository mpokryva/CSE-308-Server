package com.broncos.gerrymandering.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "STATE")
public class State implements Serializable {
    @Id
    @GeneratedValue
    private int id;
    @Column(name = "STATE_CODE")
    private String stateCode;
    @Column(name = "NAME")
    private String name;
    @Column(name = "BOUNDARY")
    private String boundary;
    @Column(name = "CONSTITUTION_TEXT")
    private String constitutionText;

    public State() {

    }

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("broncos");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        State s = new State();
        s.name = "TestName";
        em.persist(s);
        em.getTransaction().commit();
    }
}