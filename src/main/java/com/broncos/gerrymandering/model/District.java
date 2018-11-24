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
    private Set<Precinct> precincts;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "district")
    @MapKey(name = "year")
    @Where(clause = "PRECINCT_ID IS NULL")
    private Map<Short, Election> electionsByYear;

    public District() {
    }

    public int getDistrictId() {
        return districtId;
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
        return String.format("[%d]: %s", id, state.getName());
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("broncos");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        District d = em.find(District.class, 169);
        em.getTransaction().commit();
        System.out.println(d);
        System.out.println(d.getGeometry());
//        for (Precinct precinct : d.precincts) {
//            System.out.println(precinct);
//        }
//        for (Election election : d.electionsByYear.values()) {
//            System.out.println(election.getDemocratVotes());
//            System.out.println(election.getYear());
//        }
    }

}
