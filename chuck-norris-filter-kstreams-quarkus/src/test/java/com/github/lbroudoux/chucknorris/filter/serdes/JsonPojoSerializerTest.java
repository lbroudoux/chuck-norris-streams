package com.github.lbroudoux.chucknorris.filter.serdes;

import com.github.lbroudoux.chucknorris.filter.model.Customer;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/**
 * @author laurent
 */
public class JsonPojoSerializerTest {

    @Test
    public void testSerialization() {
        Map<String, Object> serdeProps = new HashMap<>();
        serdeProps.put("serializedClass", Customer.class);

        JsonPojoSerializer<Customer> ser = new JsonPojoSerializer<>();
        ser.configure(serdeProps, false);

        Customer customer = new Customer(null, 1, "Laurent", "Broudoux", "@lbroudoux");
        byte[] result = ser.serialize("topic", customer);
        String resultStr = new String(result);
        System.err.println("Result: " + resultStr);

        assertTrue(resultStr.indexOf("first_name") != -1);
        assertTrue(resultStr.indexOf("last_name") != -1);
        assertTrue(resultStr.indexOf("twitter_handle") != -1);
    }
}