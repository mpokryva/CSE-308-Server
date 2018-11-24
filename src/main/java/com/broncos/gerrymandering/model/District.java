package com.broncos.gerrymandering.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * Created by mpokr on 11/23/2018.
 */
@Entity(name = "DISTRICT")
public class District implements Serializable {

    @Id
    @GeneratedValue
    private int id;
    @Column(name = "DISTRICT_ID")
    private int districtId;
    @Column(name = "POPULATION")
    private int population;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "STATE_ID")
    private State state;
    @Column(name = "BOUNDARY")
    @Type(type = "text")
    private String boundary;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "REPRESENTATIVE_ID")
    private Representative representative;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "district")
    private Set<Precinct> precincts;

    public District() {
    }

    public int getDistrictId() {
        return districtId;
    }

    @Override
    public String toString() {
        return String.format("[%d]: %s", id, state.getName());
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("broncos");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        District d = em.find(District.class, 165);
        System.out.println(d);
        em.getTransaction().commit();
        for (Precinct precinct : d.precincts) {
            System.out.println(precinct);
        }
    }

}
