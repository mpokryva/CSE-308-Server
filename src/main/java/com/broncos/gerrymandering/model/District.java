package com.broncos.gerrymandering.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.*;
import org.json.JSONPropertyIgnore;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.annotation.AccessType;
import org.wololo.jts2geojson.GeoJSONReader;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Created by mpokr on 11/23/2018.
 */
@Entity(name = "DISTRICT")
public class District implements Serializable {

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

    @Override
    public String toString() {
        return String.format("[%d]: %s", id, state.getName());
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
