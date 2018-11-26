package com.broncos.gerrymandering.model;

import com.broncos.gerrymandering.util.DefaultEntityManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.locationtech.jts.geom.Geometry;
import org.wololo.jts2geojson.GeoJSONReader;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;

@Entity(name = "STATE")
public class State implements Serializable {
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

    public Map<Integer, District> getDistrictById() {
        return districtById;
    }

    public Map<Short, Election> getElectionByYear() {
        return electionByYear;
    }

    public String getBoundary() {
        return boundary;
    }

    @Override
    public String toString() {
        return String.format("[%d]: %s", id, name);
    }

}