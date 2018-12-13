package com.broncos.gerrymandering.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.broncos.gerrymandering.util.GeoValidation;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.Geometry;
import org.wololo.jts2geojson.GeoJSONReader;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by mpokr on 11/23/2018.
 */
@Entity(name = "PRECINCT")
public class Precinct implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = "PRECINCT_ID")
    private Integer precinctId;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "DISTRICT_ID", nullable = false)
    @JsonIgnore
    private District district;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "STATE_ID", nullable = false)
    @JsonIgnore
    private State state;
    @Column(name = "POPULATION")
    private Integer population;
    @Column(name = "BOUNDARY")
    @Type(type = "text")
    private String boundary;
    @JsonIgnore
    private transient Geometry geometry;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "PRECINCT_NEIGHBOR",
            joinColumns = @JoinColumn(name = "PRECINCT_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "NEIGHBOR_ID", referencedColumnName = "ID"))
    private Set<Precinct> neighbors;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "precinct", fetch = FetchType.LAZY)
    @MapKey(name = "year")
    private Map<Short, Election> electionByYear;

    public Precinct() {

    }

    public Integer getPrecinctId() {
        return precinctId;
    }

    public Geometry getGeometry() {
        if (geometry == null) {
            GeoJSONReader reader = new GeoJSONReader();
            geometry = GeoValidation.validate(reader.read(boundary));

        }
        return geometry;
    }

    public String getBoundary() {
        return boundary;
    }

    public Map<Short, Election> getElectionByYear() {
        return electionByYear;
    }

    public Set<Precinct> getNeighbors() { return neighbors; }

    public District getDistrict() { return district; }

    public void setDistrict(District district) {
        this.district = district;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Precinct getRandomNeighbor() {
        Optional<Precinct> optPrecinct = neighbors.stream()
                .skip((int) (Math.random() * neighbors.size()))
                .findFirst();
        return optPrecinct.orElseThrow(() -> new IllegalStateException("Precinct with id " + this.precinctId + " has no neighbors."));
    }

    public State getState() {
        return state;
    }

    @Override
    public String toString() {
        return String.format("[%s]: (%d, %s, %d)", id, precinctId, state.getName(), district.getDistrictId());
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("broncos");
        EntityManager em = emf.createEntityManager();
        Precinct p = em.find(Precinct.class, 1639);
        System.out.println(p);
        System.out.println(p.boundary);
    }

}
