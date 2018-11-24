package com.broncos.gerrymandering.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by mpokr on 11/23/2018.
 */
@Entity(name = "REPRESENTATIVE")
public class Representative implements Serializable  {

    @Id
    @GeneratedValue
    private int id;
    @Column(name = "NAME")
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "PARTY", columnDefinition = "enum('REPUBLICAN', 'DEMOCRAT', 'GREEN', 'LIBERTARIAN')")
    private Party party;

    public Representative() {
    }
}
