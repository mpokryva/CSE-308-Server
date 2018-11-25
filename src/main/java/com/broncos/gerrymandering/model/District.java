package com.broncos.gerrymandering.model;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.json.JSONObject;
import org.locationtech.jts.geom.Geometry;
import org.wololo.jts2geojson.GeoJSONReader;

import javax.persistence.*;
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
    private transient Geometry geometry;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "REPRESENTATIVE_ID")
    private Representative representative;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "district")
    @MapKey(name = "precinctId")
    private Map<Integer, Precinct> precinctById;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "district")
    @MapKey(name = "year")
    @Where(clause = "PRECINCT_ID IS NULL")
    private Map<Short, Election> electionByYear;

    public District() {
    }

    public int getDistrictId() {
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
