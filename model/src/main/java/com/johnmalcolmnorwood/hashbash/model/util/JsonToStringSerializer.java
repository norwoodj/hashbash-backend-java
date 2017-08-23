package com.johnmalcolmnorwood.hashbash.model.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;


public class JsonToStringSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object object, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
        gen.writeString(object.toString());
    }
}
