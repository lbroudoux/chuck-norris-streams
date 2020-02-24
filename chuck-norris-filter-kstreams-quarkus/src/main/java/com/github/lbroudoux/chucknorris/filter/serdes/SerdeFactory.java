package com.github.lbroudoux.chucknorris.filter.serdes;

import com.github.lbroudoux.chucknorris.filter.model.CustomerRentalMovieAggregate;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import java.util.HashMap;
import java.util.Map;
/**
 * @author laurent
 */
public class SerdeFactory {

    public static <T> Serde<T> createDbzEventJsonPojoSerdeFor(Class<T> clazz, boolean isKey) {
        Map<String, Object> serdeProps = new HashMap<>();
        serdeProps.put("serializedClass", clazz);

        Serializer<T> ser = new JsonPojoSerializer<>();
        ser.configure(serdeProps, isKey);

        Deserializer<T> de = new JsonHybridDeserializer<>();
        de.configure(serdeProps, isKey);

        return Serdes.serdeFrom(ser, de);
    }

    public static Serde<CustomerRentalMovieAggregate> CustomerRentalMovieAggregateSerde() {
        return new CustomerRentalMovieAggregateSerde();
    }

    public static class CustomerRentalMovieAggregateSerde implements Serde<CustomerRentalMovieAggregate> {

        Serializer<CustomerRentalMovieAggregate> ser = new JsonPojoSerializer<>();
        Deserializer<CustomerRentalMovieAggregate> de = new JsonHybridDeserializer<>();

        public CustomerRentalMovieAggregateSerde() {
            Map<String, Object> serdeProps = new HashMap<>();
            serdeProps.put("serializedClass", CustomerRentalMovieAggregate.class);

            ser.configure(serdeProps, false);
            de.configure(serdeProps, false);
        }

        @Override
        public void configure(Map<String, ?> map, boolean b) { }

        @Override
        public void close() {

        }

        @Override
        public Serializer<CustomerRentalMovieAggregate> serializer() {
            return ser;
        }

        @Override
        public Deserializer<CustomerRentalMovieAggregate> deserializer() {
            return de;
        }
    }
}