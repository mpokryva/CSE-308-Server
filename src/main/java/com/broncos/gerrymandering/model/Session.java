package com.broncos.gerrymandering.model;

import javax.persistence.*;
import java.util.UUID;

@Entity(name = "SESSION")
public class Session {

    @Id
    private UUID id;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "STATE_ID")
    private State state;
    private String username;

    public Session() {

    }

    public Session(UUID id, State state, String username) {
        this.id = id;
        this.state = state;
        this.username = username;
    }

    public UUID getId() {
        return id;
    }

    public State getState() {
        return state;
    }

    public String getUsername() {
        return username;
    }
}
