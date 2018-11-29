package com.broncos.gerrymandering.util;

import com.fasterxml.jackson.annotation.JsonRawValue;

/**
 * Created by mpokr on 11/29/2018.
 */
public class BoundaryWrapper {

    @JsonRawValue
    private String boundary;

    public BoundaryWrapper(String boundary) {
        this.boundary = boundary;
    }
}
