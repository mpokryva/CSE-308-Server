package com.broncos.gerrymandering.model;

import org.hibernate.annotations.*;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygonal;
import org.locationtech.jts.geom.prep.PreparedPolygon;
import org.springframework.util.Assert;
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
    private transient boolean measuresUpdatedOnce = false;

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

    public District cloneForSA(State state) {
        for (Precinct precinct : this.getPrecincts()) {
            if (precinct.getDistrict() == null) {
                throw new IllegalStateException("HERE");
            }
        }
        District clone = new District();
        clone.id = null;
        clone.districtId = this.districtId;
        clone.population = this.population;
        clone.state = state;
        clone.boundary = this.boundary;
        clone.geometry = this.geometry;
        clone.representative = this.representative;
        clone.precinctById = this.precinctById;
        clone.electionByYear = this.electionByYear;
        clone.isOriginal = false;
        clone.borderPrecincts = this.getBorderPrecincts();
        return clone;
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

    public State getState() {
        return state;
    }

    public Precinct getPrecinctById(Integer precinctId) {
        return precinctById.get(precinctId);
    }

    public Set<Precinct> getPrecincts() {
        return new HashSet<>(precinctById.values());
    }

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

    public void setBoundary(String boundary) {
        this.boundary = boundary;
    }

    public String getBoundary() {
        return boundary;
    }

    public Integer getPopulation() {
        return population;
    }

    public Set<Precinct> getBorderPrecincts() {
        if (borderPrecincts == null) {
            PreparedPolygon prepDistrict = new PreparedPolygon((Polygonal) getGeometry());
            if (borderPrecincts != null)
                borderPrecincts.clear();
            else
                borderPrecincts = new HashSet<>();
            for (Precinct precinct : precinctById.values()) {
                if (!prepDistrict.containsProperly(precinct.getGeometry()))
                    borderPrecincts.add(precinct);
            }
        }
        return borderPrecincts;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return String.format("[%d]: %s", id, state.getName());
    }

    private void updateBorderPrecincts(Precinct precinct, boolean isAdded) {
        if (borderPrecincts == null)
            getBorderPrecincts();
        if (isAdded)
            borderPrecincts.add(precinct);
        else
            borderPrecincts.remove(precinct);
        PreparedPolygon prepDistrict = new PreparedPolygon((Polygonal) getGeometry());
        if (isAdded)
            for (Precinct neighbor : precinct.getNeighbors()) {
                if (borderPrecincts.contains(neighbor) && prepDistrict.containsProperly(neighbor.getGeometry()))
                    borderPrecincts.remove(neighbor);
            }
        else {
            for (Precinct neighbor : precinct.getNeighbors()) {
                if (precinctById.containsKey(neighbor.getPrecinctId()) && !prepDistrict.containsProperly(neighbor.getGeometry()))
                    borderPrecincts.add(neighbor);
            }
        }
    }

    public double calculateWastedVotes() {
        Election currElection = electionByYear.get(CURRENT_YEAR);
        int excessVotes = Math.max(currElection.getDemocratVotes(), currElection.getRepublicanVotes())
                - ((currElection.getDemocratVotes() + currElection.getRepublicanVotes()) / 2);
        int lostVotes = Math.min(currElection.getDemocratVotes(), currElection.getRepublicanVotes());
        return excessVotes - lostVotes;
    }

    public double getDemocractRepublicanVotesRatio() {
        Election currElection = electionByYear.get(CURRENT_YEAR);
        return (double) currElection.getDemocratVotes() / currElection.getRepublicanVotes();
    }

    public double calculateCompactness() {
        double area = getGeometry().getArea();
        double perimeter = getGeometry().getLength();
        return (4 * Math.PI * area) / Math.pow(perimeter, 2);
    }

    public void addPrecinct(Precinct precinct) {
        precinctById.put(precinct.getPrecinctId(), precinct);
        precinct.setDistrict(this);
        precinct.setState(this.state);
        //check if geometry is MultiPolygon
        if (boundary == null) {
            boundary = precinct.getBoundary();
            geometry = precinct.getGeometry();
        } else {
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
    }

    public void removePrecinct(Precinct precinct) {
        Precinct p  = precinctById.remove(precinct.getPrecinctId());
        p.setDistrict(null);
        p.setState(null);
        //check if geometry is MultiPolygon
        geometry = getGeometry().difference(p.getGeometry());
        GeoJSONWriter writer = new GeoJSONWriter();
        boundary = writer.write(geometry).toString();
        Election distElection = electionByYear.get(CURRENT_YEAR);
        Election precElection = p.getElectionByYear().get(CURRENT_YEAR);
        distElection.setDemocratVotes(
                distElection.getDemocratVotes() - precElection.getDemocratVotes());
        distElection.setRepublicanVotes(
                distElection.getRepublicanVotes() - precElection.getRepublicanVotes());
        distElection.setVotingAgePopulation(
                distElection.getVotingAgePopulation() - precElection.getVotingAgePopulation());
        population -= precElection.getVotingAgePopulation(); //TODO: DO SOMETHING ABOUT NAMING
        updateBorderPrecincts(p, false);
    }

}
