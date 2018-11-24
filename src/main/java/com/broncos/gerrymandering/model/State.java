package com.broncos.gerrymandering.model;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.hibernate.type.TextType;
import org.json.JSONObject;
import org.locationtech.jts.geom.Geometry;
import org.wololo.jts2geojson.GeoJSONReader;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

@Entity(name = "STATE")
public class State implements Serializable {
    @Id
    @GeneratedValue
    private int id;
    @Column(name = "STATE_CODE", columnDefinition = "char")
    private String stateCode;
    @Column(name = "NAME")
    private String name;
    @Column(name = "BOUNDARY")
    @Type(type = "text")
    private String boundary;
    private transient Geometry geometry;
    @Column(name = "CONSTITUTION_TEXT")
    @Type(type = "text")
    private String constitutionText;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "state")
    private Set<District> districts;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "state")
    @MapKey(name = "year")
    @Where(clause = "PRECINCT_ID IS NULL AND DISTRICT_ID IS NULL")
    private Map<Short, Election> electionsByYear;

    public State() {
    }

    public String getName() {
        return name;
    }

    public Geometry getGeometry() {
        if (geometry == null) {
            GeoJSONReader reader = new GeoJSONReader();
            JSONObject json = new JSONObject(boundary);
            geometry = reader.read(json.getJSONObject("geometry").toString());
        }
        return geometry;
    }

    @Override
    public String toString() {
        return String.format("[%d]: %s", id, name);
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("broncos");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        State s1 = em.find(State.class, 36);
        em.getTransaction().commit();
        System.out.println(s1);
        for (District d : s1.districts) {
            System.out.println(d.getDistrictId());
        }
        for (Election election : s1.electionsByYear.values()) {
            System.out.println(election.getDemocratVotes());
            System.out.println(election.getYear());
        }
    }


}