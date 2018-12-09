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
    private Integer id;
    @Column(name = "VOTING_AGE_POPULATION")
    private Integer votingAgePopulation;
    @Column(name = "REP_VOTES")
    private Integer republicanVotes;
    @Column(name = "DEM_VOTES")
    private Integer democratVotes;
    @Column(name = "YEAR")
    private short year;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "STATE_ID")
    private State state;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "DISTRICT_ID")
    private District district;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "PRECINCT_ID")
    private Precinct precinct;

    public Election() {
    }

    public Election(Integer votingAgePopulation, Integer republicanVotes, Integer democratVotes, short year) {
        this.votingAgePopulation = votingAgePopulation;
        this.republicanVotes = republicanVotes;
        this.democratVotes = democratVotes;
        this.year = year;
    }

    public Integer getRepublicanVotes() {
        return republicanVotes;
    }

    public Integer getDemocratVotes() {
        return democratVotes;
    }

    public short getYear() {
        return year;
    }

    public Integer getVotingAgePopulation() {
        return votingAgePopulation;
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("broncos");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Election e = em.find(Election.class, 229);
        System.out.println(e.precinct);
    }

    public void setVotingAgePopulation(Integer votingAgePopulation) {
        this.votingAgePopulation = votingAgePopulation;
    }

    public void setRepublicanVotes(Integer republicanVotes) {

        this.republicanVotes = republicanVotes;
    }

    public void setDemocratVotes(Integer democratVotes) {
        this.democratVotes = democratVotes;
    }
}
