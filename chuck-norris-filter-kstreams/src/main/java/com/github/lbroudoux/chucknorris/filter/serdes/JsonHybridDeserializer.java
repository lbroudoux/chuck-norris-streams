package com.github.lbroudoux.chucknorris.filter.serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import com.github.lbroudoux.chucknorris.filter.model.EventType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonHybridDeserializer<T> implements Deserializer<T> {

    public static final String DBZ_CDC_EVENT_PAYLOAD_FIELD = "payload";
    public static final String DBZ_CDC_EVENT_AFTER_FIELD = "after";

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private Class<T> clazz;

    @SuppressWarnings("unchecked")
    @Override
    public void configure(Map<String, ?> props, boolean isKey) {
        clazz = (Class<T>)props.get("serializedClass");
    }

    @Override
    public T deserialize(String topic, byte[] bytes) {

        if (bytes == null)
            return null;

        T data;

        //step1: try to directly deserialize expected class
        try {
            data = OBJECT_MAPPER.readValue(bytes, clazz);
        } catch (IOException e1) {
            //step2: try to deserialize from DBZ json event
            try {
                Map temp = (Map) OBJECT_MAPPER.readValue(new String(bytes), Map.class)
                        .get(DBZ_CDC_EVENT_PAYLOAD_FIELD);
                if (temp == null) {
                    temp = new HashMap();
                    //NOTE: we record a delete event in this case
                    temp.put("_eventType", EventType.DELETE);
                }
                // TODO
                //data = OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsBytes(temp), clazz);
                data = OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsBytes(temp.get(DBZ_CDC_EVENT_AFTER_FIELD)), clazz);
                if (data != null) {
                    System.err.println("Just create a new: " + data.toString());
                } else {
                    System.err.println("Create a null object...");
                }
            } catch(IOException e2) {
                throw new SerializationException(e2);
            }
        }

        return data;
    }

    @Override
    public void close() { }
}
