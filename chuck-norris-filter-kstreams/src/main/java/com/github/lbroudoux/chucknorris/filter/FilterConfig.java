package com.github.lbroudoux.chucknorris.filter;

import com.github.lbroudoux.chucknorris.filter.model.CustomerRentalMovieAggregate;
import com.github.lbroudoux.chucknorris.filter.serdes.SerdeFactory;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
/**
 * @author laurent
 */
public class FilterConfig {

    private static final Logger log = LoggerFactory.getLogger(FilterConfig.class.getName());

    private final String bootstrapServers;
    private final String rentalsSourceTopic;
    private final String moviesSourceTopic;
    private final String customersSourceTopic;
    private final String targetTopic;
    private final String trustStorePassword;
    private final String trustStorePath;
    private final String keyStorePassword;
    private final String keyStorePath;
    private final String username;
    private final String password;

    public FilterConfig(String bootstrapServers, String rentalsSourceTopic, String moviesSourceTopic, String customersSourceTopic, String targetTopic, String trustStorePassword, String trustStorePath, String keyStorePassword, String keyStorePath, String username, String password) {
        this.bootstrapServers = bootstrapServers;
        this.rentalsSourceTopic = rentalsSourceTopic;
        this.moviesSourceTopic = moviesSourceTopic;
        this.customersSourceTopic = customersSourceTopic;
        this.targetTopic = targetTopic;
        this.trustStorePassword = trustStorePassword;
        this.trustStorePath = trustStorePath;
        this.keyStorePassword = keyStorePassword;
        this.keyStorePath = keyStorePath;
        this.username = username;
        this.password = password;
    }

    public static FilterConfig fromEnv() {
        String bootstrapServers = System.getenv("BOOTSTRAP_SERVERS");
        String rentalsSourceTopic = System.getenv("RENTALS_SOURCE_TOPIC");
        String moviesSourceTopic = System.getenv("MOVIES_SOURCE_TOPIC");
        String customersSourceTopic = System.getenv("CUSTOMERS_SOURCE_TOPIC");
        String targetTopic = System.getenv("TARGET_TOPIC");
        String trustStorePassword = System.getenv("TRUSTSTORE_PASSWORD") == null ? null : System.getenv("TRUSTSTORE_PASSWORD");
        String trustStorePath = System.getenv("TRUSTSTORE_PATH") == null ? null : System.getenv("TRUSTSTORE_PATH");
        String keyStorePassword = System.getenv("KEYSTORE_PASSWORD") == null ? null : System.getenv("KEYSTORE_PASSWORD");
        String keyStorePath = System.getenv("KEYSTORE_PATH") == null ? null : System.getenv("KEYSTORE_PATH");
        String username = System.getenv("USERNAME") == null ? null : System.getenv("USERNAME");
        String password = System.getenv("PASSWORD") == null ? null : System.getenv("PASSWORD");

        return new FilterConfig(bootstrapServers, rentalsSourceTopic, moviesSourceTopic, customersSourceTopic, targetTopic, trustStorePassword, trustStorePath, keyStorePassword, keyStorePath, username, password);

        //return new FilterConfig("localhost:9092", "rental.rentals", "rental.movies",
        //      "rental.customers", "rental.chucknorris", trustStorePassword, trustStorePath, keyStorePassword, keyStorePath, username, password);
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }
    public String getRentalsTopic() {
        return rentalsSourceTopic;
    }
    public String getMoviesTopic() {
        return moviesSourceTopic;
    }
    public String getCustomersTopic() {
        return customersSourceTopic;
    }
    public String getTargetTopic() {
        return targetTopic;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }
    public String getTrustStorePath() {
        return trustStorePath;
    }
    public String getKeyStorePassword() {
        return keyStorePassword;
    }
    public String getKeyStorePath() {
        return keyStorePath;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }

    public static Properties getKafkaStreamsProperties(FilterConfig config) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "chuck-norris-filter");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 5000);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SerdeFactory.CustomerRentalMovieAggregateSerde().getClass());

        if (config.getTrustStorePassword() != null && config.getTrustStorePath() != null)   {
            log.info("Configuring truststore");
            props.put("security.protocol", "SSL");
            props.put("ssl.truststore.type", "PKCS12");
            props.put("ssl.truststore.password", config.getTrustStorePassword());
            props.put("ssl.truststore.location", config.getTrustStorePath());
        }

        if (config.getKeyStorePassword() != null && config.getKeyStorePath() != null)   {
            log.info("Configuring keystore");
            props.put("security.protocol", "SSL");
            props.put("ssl.keystore.type", "PKCS12");
            props.put("ssl.keystore.password", config.getKeyStorePassword());
            props.put("ssl.keystore.location", config.getKeyStorePath());
        }

        if (config.getUsername() != null && config.getPassword() != null)   {
            props.put("sasl.mechanism","SCRAM-SHA-512");
            props.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"" + config.getUsername() + "\" password=\"" + config.getPassword() + "\";");

            if (props.get("security.protocol") != null && props.get("security.protocol").equals("SSL"))  {
                props.put("security.protocol","SASL_SSL");
            } else {
                props.put("security.protocol","SASL_PLAINTEXT");
            }
        }

        return props;
    }

}