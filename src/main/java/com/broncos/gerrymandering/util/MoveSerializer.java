package com.broncos.gerrymandering.util;

import com.broncos.gerrymandering.algorithm.Move;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * Created by kristiancharbonneau on 12/4/18.
 */
@JsonComponent
public class MoveSerializer extends JsonSerializer<Move> {

    @Override
    public void serialize(Move move, JsonGenerator json,
                          SerializerProvider serializerProvider) throws IOException {
        json.writeStartObject();
        json.writeNumberField("precinctId", move.getPrecinct().getPrecinctId());
        json.writeNumberField("sourceDistrictId", move.getSource() == null ?
                                -1 : move.getSource().getDistrictId());
//        json.writeStringField("sourceGeometry", move.getSource() == null ?
//                                "" : move.getSource().getBoundary());
        json.writeNumberField("destinationDistrictId", move.getDestination().getDistrictId());
//        json.writeStringField("destinationGeometry", move.getDestination().getBoundary());
        json.writeNumberField("objFuncValue", move.getObjFuncVal());
        json.writeEndObject();

    }

}
