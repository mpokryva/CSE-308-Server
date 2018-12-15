package com.broncos.gerrymandering.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.*;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygonal;
import org.locationtech.jts.geom.prep.PreparedPolygon;
import org.springframework.format.number.money.CurrencyUnitFormatter;
import org.wololo.jts2geojson.GeoJSONReader;
import org.wololo.jts2geojson.GeoJSONWriter;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.criteria.CriteriaBuilder;
import java.io.Serializable;
import java.util.*;

/**
 * Created by mpokr on 11/23/2018.
 */
@Entity(name = "DISTRICT")
public class District implements Serializable {
    private static final Short CURRENT_YEAR = 2010;
    private static final int TWO = 2;

    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = "DISTRICT_ID")
    private Integer districtId;
    @Column(name = "POPULATION")
    private Integer population;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "STATE_ID")
    private State state;
    @Column(name = "BOUNDARY")
    @Type(type = "text")
    private String boundary;
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
    @Column(name = "ORIGINAL")
    private Boolean isOriginal;
    private transient Set<Precinct> borderPrecincts;
    private transient Map<Measure, Double> valueByMeasure;

    public District() {
    }

    public District(int districtId, State state, boolean isOriginal) {
        this.districtId = districtId;
        this.state = state;
        this.isOriginal = isOriginal;
        precinctById = new HashMap<>();
        population = 0;
        electionByYear = new HashMap<>();
        electionByYear.put(CURRENT_YEAR, new Election(0, 0, 0, CURRENT_YEAR));

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

    public Set<Precinct> getPrecincts() { return new HashSet<>(precinctById.values()); }

    public Boolean getOriginal() {
        return isOriginal;
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

    public void setBoundary(String boundary) { this.boundary = boundary; }

    public String getBoundary() {
        return boundary;
    }

    public Integer getPopulation() { return population; }

    public Set<Precinct> getBorderPrecincts() {
        if(borderPrecincts == null) {
            PreparedPolygon prepDistrict = new PreparedPolygon((Polygonal)getGeometry());
            if(borderPrecincts != null)
                borderPrecincts.clear();
            else
                borderPrecincts = new HashSet<>();
            for(Precinct precinct: precinctById.values()) {
                if(!prepDistrict.containsProperly(precinct.getGeometry()))
                    borderPrecincts.add(precinct);
            }
        }
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

    private void updateBorderPrecincts(Precinct precinct, boolean isAdded) {
        if(borderPrecincts == null)
            getBorderPrecincts();
        if(isAdded)
            borderPrecincts.add(precinct);
        else
            borderPrecincts.remove(precinct);
        PreparedPolygon prepDistrict = new PreparedPolygon((Polygonal)getGeometry());
        if(isAdded)
            for(Precinct neighbor: precinct.getNeighbors()) {
                if(borderPrecincts.contains(neighbor) && prepDistrict.containsProperly(neighbor.getGeometry()))
                    borderPrecincts.remove(neighbor);
            }
        else {
            for(Precinct neighbor: precinct.getNeighbors()) {
                if(precinctById.containsKey(precinct.getPrecinctId()) && !prepDistrict.containsProperly(neighbor.getGeometry()))
                    borderPrecincts.add(neighbor);
            }
        }
    }

    private void updateMeasures() {
        if(valueByMeasure == null) valueByMeasure = new HashMap<>();
        for(Measure measure: Measure.values()) {
            Election currElection = electionByYear.get(CURRENT_YEAR);
            switch (measure) {
                case EFFICIENCY_GAP:
                    int excessVotes = Math.max(currElection.getDemocratVotes(), currElection.getRepublicanVotes())
                            - ((currElection.getDemocratVotes() + currElection.getRepublicanVotes()) / TWO);
                    int lostVotes = Math.min(currElection.getDemocratVotes(), currElection.getRepublicanVotes());
                    valueByMeasure.put(measure, (double) (excessVotes - lostVotes));
                    break;
                case COMPACTNESS:
                    double area = getGeometry().getArea();
                    double perimeter = getGeometry().getLength();
                    double polsbyPopper = (4 * Math.PI * area) / Math.pow(perimeter, 2);
                    valueByMeasure.put(measure, polsbyPopper);
                    break;
                case PARTISAN_FAIRNESS:
                    double a = (double)currElection.getDemocratVotes() / currElection.getRepublicanVotes();
                    valueByMeasure.put(measure,
                            (double)currElection.getDemocratVotes() / currElection.getRepublicanVotes());
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
        precinct.setDistrict(this);
        precinct.setState(this.state);
        //check if geometry is MultiPolygon
        if (boundary == null) {
            boundary = precinct.getBoundary();
            geometry = precinct.getGeometry();
        }else {
            geometry = getGeometry().union(precinct.getGeometry());
        }
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
        updateBorderPrecincts(precinct, true);
        updateMeasures();
    }

    public void removePrecinct(Precinct precinct) {
        precinctById.remove(precinct.getPrecinctId());
        precinct.setDistrict(null);
        precinct.setState(null);
        //check if geometry is MultiPolygon
        geometry = getGeometry().difference(precinct.getGeometry());
        GeoJSONWriter writer = new GeoJSONWriter();
        boundary = writer.write(geometry).toString();
        Election distElection = electionByYear.get(CURRENT_YEAR);
        Election precElection = precinct.getElectionByYear().get(CURRENT_YEAR);
        distElection.setDemocratVotes(
                distElection.getDemocratVotes() - precElection.getDemocratVotes());
        distElection.setRepublicanVotes(
                distElection.getRepublicanVotes() - precElection.getRepublicanVotes());
        distElection.setVotingAgePopulation(
                distElection.getVotingAgePopulation() - precElection.getVotingAgePopulation());
        population -= precElection.getVotingAgePopulation(); //TODO: DO SOMETHING ABOUT NAMING
        updateBorderPrecincts(precinct, false);
        updateMeasures();
    }

}
