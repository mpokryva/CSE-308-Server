package com.broncos.gerrymandering.model;

import org.hibernate.annotations.Type;
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
@Entity(name = "PRECINCT")
public class Precinct implements Serializable {
    @Id
    @GeneratedValue
    private int id;
    @Column(name = "PRECINCT_ID")
    private int precinctId;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "DISTRICT_ID")
    private District district;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "STATE_ID")
    private State state;
    @Column(name = "POPULATION")
    private int population;
    @Column(name = "BOUNDARY")
    @Type(type = "text")
    private String boundary;
    private transient Geometry geometry;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "PRECINCT_NEIGHBOR",
            joinColumns = @JoinColumn(name = "PRECINCT_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "NEIGHBOR_ID", referencedColumnName = "ID"))
    private Set<Precinct> neighbors;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "precinct")
    @MapKey(name = "year")
    private Map<Short, Election> electionsByYear;

    public Precinct() {

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
        return String.format("[%s]: (%s, %d)", id, state.getName(), district.getDistrictId());
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("broncos");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Precinct p = em.find(Precinct.class, 1639);
        em.getTransaction().commit();
        System.out.println(p);
        System.out.println(p.boundary);
//        for (Precinct neighbor : p.neighbors) {
//            System.out.println(neighbor);
//        }
//        for (Election election : p.electionsByYear.values()) {
//            System.out.println(election.getDemocratVotes());
//            System.out.println(election.getYear());
//        }
    }

}
