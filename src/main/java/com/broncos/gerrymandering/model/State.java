package com.broncos.gerrymandering.model;

import com.broncos.gerrymandering.util.DefaultEntityManager;
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
    private int id;
    @Column(name = "STATE_CODE", columnDefinition = "char")
    @Enumerated(EnumType.STRING)
    private StateCode stateCode;
    @Column(name = "NAME")
    private String name;
    @Column(name = "BOUNDARY")
    @Type(type = "text")
    private String boundary;
    private transient Geometry geometry;
    @Column(name = "CONSTITUTION_TEXT")
    @Type(type = "text")
    private String constitutionText;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "state", fetch = FetchType.LAZY)
    @MapKey(name = "districtId")
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

    @Override
    public String toString() {
        return String.format("[%d]: %s", id, name);
    }

    public static void main(String[] args) {
        EntityManager em = new DefaultEntityManager().getDefaultEntityManager();
        State s1 = em.find(State.class, 36);
        System.out.println(s1);
        for (District d : s1.districtById.values()) {
            System.out.println(d.getDistrictId());
        }
        for (Election election : s1.electionByYear.values()) {
            System.out.println(election.getDemocratVotes());
            System.out.println(election.getYear());
        }
    }


}