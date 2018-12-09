package com.broncos.gerrymandering.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.locationtech.jts.geom.Geometry;
import org.wololo.jts2geojson.GeoJSONReader;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Entity(name = "STATE")
public class State implements Serializable {
    private static final Short CURRENT_YEAR = 2010;
    private static final int ZERO = 0;

    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = "STATE_CODE", columnDefinition = "char")
    @Enumerated(EnumType.STRING)
    private StateCode stateCode;
    @Column(name = "NAME")
    private String name;
    @Column(name = "BOUNDARY")
    @Type(type = "text")
    private String boundary;
    @JsonIgnore
    private transient Geometry geometry;
    @Column(name = "CONSTITUTION_TEXT")
    @Type(type = "text")
    private String constitutionText;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "state", fetch = FetchType.LAZY)
    @MapKey(name = "districtId")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private Map<Integer, District> districtById;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "state", fetch = FetchType.LAZY)
    @MapKey(name = "year")
    @Where(clause = "PRECINCT_ID IS NULL AND DISTRICT_ID IS NULL")
    private Map<Short, Election> electionByYear;
    @Column(name = "ORIGINAL")
    private Boolean isOriginal;

    public State() {
    }

    public String getName() {
        return name;
    }

    public Geometry getGeometry() {
        if (geometry == null) {
            GeoJSONReader reader = new GeoJSONReader();
            geometry = reader.read(boundary);
        }
        return geometry;
    }

    public District getDistrictById(Integer districtId) {
        return districtById.get(districtId);
    }

    public Map<Integer, District> getDistrictMap() {
        return districtById;
    }

    public Set<District> getDistricts() {
        return new HashSet<>(districtById.values());
    }

    public Map<Short, Election> getElectionByYear() {
        return electionByYear;
    }

    public String getBoundary() {
        return boundary;
    }

    public Boolean isOriginal() {
        return isOriginal;
    }

    public State cloneForRG() {
        State state = new State();
        state.stateCode = stateCode;
        state.name = name;
        state.boundary = boundary;
        state.geometry = geometry;
        state.constitutionText = constitutionText;
        state.districtById = new ConcurrentHashMap<>();
        state.electionByYear = new HashMap<>(electionByYear);
        state.isOriginal = false;
        return state;
    }


    public District getRandomDistrict() {
        Optional<District> optDistrict = districtById.values()
                .stream()
                .skip((int) (Math.random() * districtById.size()))
                .findFirst();
        return optDistrict.orElseThrow(() -> new IllegalStateException("No districts " +
                "in state with code " + this.stateCode));
    }

    public double getObjFuncVal(Map<Measure, Double> weights) {
        double wastedVoteDifferenceTotal = 0;
        double polsbySum = 0;
        for (District district : districtById.values()) {
            for (Measure measure : Measure.values()) {
                switch (measure) {
                    case EFFICIENCY_GAP:
                        wastedVoteDifferenceTotal += district.getValueByMeasure(measure);
                        break;
                    case COMPACTNESS:
                        polsbySum += district.getValueByMeasure(measure);
                        break;
                }
            }
        }
        int population = electionByYear.get(CURRENT_YEAR).getVotingAgePopulation();
        double effGapTerm = 1 - ((Math.abs(wastedVoteDifferenceTotal) / population) * weights.get(Measure.EFFICIENCY_GAP));
        double compTerm = polsbySum / getDistricts().size();
        return effGapTerm + compTerm;
    }

    public void addDistrict(District district) {
        districtById.put(district.getDistrictId(), district);
    }

    @Override
    public String toString() {
        return String.format("[%d]: %s", id, name);
    }

}