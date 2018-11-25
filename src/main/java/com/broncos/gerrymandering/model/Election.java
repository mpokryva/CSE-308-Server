package com.broncos.gerrymandering.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by mpokr on 11/23/2018.
 */
@Entity(name = "ELECTION")
public class Election implements Serializable {

    @Id
    @GeneratedValue
    private int id;
    @Column(name = "VOTING_AGE_POPULATION")
    private int votingAgePopulation;
    @Column(name = "REP_VOTES")
    private int republicanVotes;
    @Column(name = "DEM_VOTES")
    private int democratVotes;
    @Column(name = "YEAR")
    private short year;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "STATE_ID")
    private State state;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "DISTRICT_ID")
    private District district;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PRECINCT_ID")
    private Precinct precinct;

    public Election() {
    }

    public int getRepublicanVotes() {
        return republicanVotes;
    }

    public int getDemocratVotes() {
        return democratVotes;
    }

    public short getYear() {
        return year;
    }

    public int getVotingAgePopulation() {
        return votingAgePopulation;
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("broncos");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Election e = em.find(Election.class, 229);
        System.out.println(e.precinct);
    }

}
