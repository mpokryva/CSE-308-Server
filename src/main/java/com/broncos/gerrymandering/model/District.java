package com.broncos.gerrymandering.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.*;
import org.json.JSONPropertyIgnore;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygonal;
import org.locationtech.jts.geom.prep.PreparedPolygon;
import org.springframework.data.annotation.AccessType;
import org.wololo.jts2geojson.GeoJSONReader;
import org.wololo.jts2geojson.GeoJSONWriter;
import com.broncos.gerrymandering.model.Party;
import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by mpokr on 11/23/2018.
 */
@Entity(name = "DISTRICT")
public class District implements Serializable {
    private static final Short CURRENT_YEAR = 2010;

    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = "DISTRICT_ID")
    private Integer districtId;
    @Column(name = "POPULATION")
    private Integer population;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "STATE_ID")
    @JsonIgnore
    private State state;
    @Column(name = "BOUNDARY")
    @Type(type = "text")
    private String boundary;
    @JsonIgnore
    private transient Geometry geometry;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "REPRESENTATIVE_ID")
    private Representative representative;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "district", fetch = FetchType.LAZY)
    @MapKey(name = "precinctId")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private Map<Integer, Precinct> precinctById;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "district", fetch = FetchType.LAZY)
    @Column(nullable = false)
    private Set<Precinct> precincts;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "district", fetch = FetchType.LAZY)
    @MapKey(name = "year")
    @Where(clause = "PRECINCT_ID IS NULL")
    private Map<Short, Election> electionByYear;
    private transient Set<Precinct> borderPrecincts;
    private transient Map<Measure, Double> valByMeasure;

    public District() {
    }

    public Integer getDistrictId() {
        return districtId;
    }

    public Geometry getGeometry() {
        if (geometry == null) {
            GeoJSONReader reader = new GeoJSONReader();
            geometry = reader.read(boundary);
        }
        return geometry;
    }

    public State getState() { return state; }

    public Map<Integer, Precinct> getPrecinctById() {
        return precinctById;
    }

    public Map<Short, Election> getElectionByYear() {
        return electionByYear;
    }

    public String getBoundary() {
        return boundary;
    }

    public Set<Precinct> getPrecincts() {
        return precincts;
    }

    public Set<Precinct> getBorderPrecincts() {
        if(borderPrecincts == null) updateBorderPrecincts();
        return borderPrecincts;
    }

    @Override
    public String toString() {
        return String.format("[%d]: %s", id, state.getName());
    }

    public void updateBorderPrecincts() {
        PreparedPolygon prepDistrict = new PreparedPolygon((Polygonal)getGeometry());
        if(borderPrecincts != null)
            borderPrecincts.clear();
        else
            borderPrecincts = new HashSet<>();
        for(Precinct precinct: precincts) {
            if(!prepDistrict.containsProperly(precinct.getGeometry()))
                borderPrecincts.add(precinct);
        }
    }

    public void updateMeasures() {


    }

    public void addPrecinct(Precinct precinct) {
        precincts.add(precinct);
        //check if geometry is MultiPolygon
        geometry = getGeometry().union(precinct.getGeometry());
        GeoJSONWriter writer = new GeoJSONWriter();
        boundary = writer.write(geometry).toString();
        Election distElection = electionByYear.get(CURRENT_YEAR);
        Election precElection = precinct.getElectionByYear().get(CURRENT_YEAR);
        distElection.setDemocratVotes(
                distElection.getDemocratVotes() + precElection.getDemocratVotes());
        distElection.setRepublicanVotes(
                distElection.getRepublicanVotes() + precElection.getRepublicanVotes());
        distElection.setVotingAgePopulation(
                distElection.getVotingAgePopulation() + precElection.getVotingAgePopulation());
        population += precElection.getVotingAgePopulation(); //TODO: DO SOMETHING ABOUT NAMING
        updateBorderPrecincts();
        updateMeasures();
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("broncos");
        EntityManager em = emf.createEntityManager();
        District d = em.find(District.class, 5);
        System.out.println(d.boundary);
        System.out.println(d.getGeometry());
//        for (Precinct precinct : d.precinctById) {
//            System.out.println(precinct);
//        }
//        for (Election election : d.electionByYear.values()) {
//            System.out.println(election.getDemocratVotes());
//            System.out.println(election.getYear());
//        }
    }

}
