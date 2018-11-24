package com.broncos.gerrymandering.model;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.hibernate.type.TextType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity(name = "STATE")
public class State implements Serializable {
    @Id
    @GeneratedValue
    private int id;
    @Column(name = "STATE_CODE", columnDefinition = "char")
    private String stateCode;
    @Column(name = "NAME")
    private String name;
    @Column(name = "BOUNDARY")
    @Type(type = "text")
    private String boundary;
    @Column(name = "CONSTITUTION_TEXT")
    @Type(type = "text")
    private String constitutionText;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "state")
    private Set<District> districts;

    public State() {
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("[%d]: %s", id, name);
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("broncos");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        State s = new State();
        s.name = "TestName";
        em.persist(s);
        State s1 = em.find(State.class, 28);
        System.out.println(s1);
        for (District d : s1.districts) {
            System.out.println(d.getDistrictId());
        }
        em.getTransaction().commit();
    }


}