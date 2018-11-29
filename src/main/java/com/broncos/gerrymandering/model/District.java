package com.broncos.gerrymandering.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.*;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygonal;
import org.locationtech.jts.geom.prep.PreparedPolygon;
import org.wololo.jts2geojson.GeoJSONReader;
import org.wololo.jts2geojson.GeoJSONWriter;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.*;

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
    @MapKey(name = "year")
    @Where(clause = "PRECINCT_ID IS NULL")
    private Map<Short, Election> electionByYear;
    private transient Set<Precinct> borderPrecincts;
    private transient Map<Measure, Double> valueByMeasure;

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

    public Precinct getPrecinctById(Integer precinctId) {
        return precinctById.get(precinctId);
    }

    public Iterator<Precinct> precinctIterator() {
        return precinctById.values().iterator();
    }

    public Precinct getRandomPrecinct() {
        Optional<Precinct> optPrecinct = precinctById.values().stream()
                .skip((int) (precinctById.size() * Math.random()))
                .findFirst();
        return optPrecinct.orElseThrow(() ->
                new IllegalStateException("No precincts found in district with id " + this.districtId));
    }

    public Precinct getRandomBorderPrecinct() {
        Optional<Precinct> optPrecinct = borderPrecincts.stream()
                .skip((int) (Math.random() * borderPrecincts.size()))
                .findFirst();
        return optPrecinct.orElseThrow(() ->
                new IllegalStateException("No precincts found in district with id " + this.districtId));
    }

    public Map<Short, Election> getElectionByYear() {
        return electionByYear;
    }


    public String getBoundary() {
        return boundary;
    }

    public Set<Precinct> getBorderPrecincts() {
        if(borderPrecincts == null) updateBorderPrecincts();
        return borderPrecincts;
    }

    public Double getValueByMeasure(Measure measure) {
        if(valueByMeasure == null) updateMeasures();
        return valueByMeasure.get(measure);
    }

    public void setState(State state) {
        this.state = state;
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
        Iterator<Precinct> it = precinctIterator();
        while (it.hasNext()) {
            Precinct precinct = it.next();
            if(!prepDistrict.containsProperly(precinct.getGeometry()))
                borderPrecincts.add(precinct);
        }
    }

    public void updateMeasures() {
        if(valueByMeasure == null) valueByMeasure = new HashMap<>();
        for(Measure measure: Measure.values()) {
            switch (measure) {
                case EFFICIENCY_GAP:
                    Election currElection = electionByYear.get(CURRENT_YEAR);
                    int wastedVotes = Math.max(currElection.getDemocratVotes(), currElection.getRepublicanVotes())
                            - ((currElection.getDemocratVotes() + currElection.getRepublicanVotes()) / 2);
                    int lostVotes = Math.min(currElection.getDemocratVotes(), currElection.getRepublicanVotes());
                    valueByMeasure.put(measure, Double.valueOf(wastedVotes - lostVotes));
                    break;
            }
        }

    }

    public double calculateObjFuncValue(Map<Measure, Double> weights) {
        double objFuncValue = 0;
        for (Measure measure : Measure.values()) {
            objFuncValue += valueByMeasure.get(measure) * weights.get(measure);
        }
        return objFuncValue;
    }

    public void addPrecinct(Precinct precinct) {
        precinctById.put(precinct.getPrecinctId(), precinct);
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
