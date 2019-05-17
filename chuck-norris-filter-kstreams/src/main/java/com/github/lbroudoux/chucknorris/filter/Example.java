package com.github.lbroudoux.chucknorris.filter;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.Arrays;
import java.util.Properties;

/**
 * @author laurent
 */
public class Example {

   public static void main(final String[] args) {

      Properties props = new Properties();
      props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application");
      props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
      props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
      props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

      StreamsBuilder builder = new StreamsBuilder();
      KStream<String, String> textLines = builder.stream("streams-plaintext-input");
      KTable<String, Long> wordCounts = textLines
            .flatMapValues(textLine -> Arrays.asList(textLine.toLowerCase().split("\\W+")))
            .groupBy((key, word) -> word)
            .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("counts-store"));
      wordCounts.toStream().to("streams-wordcount-output", Produced.with(Serdes.String(), Serdes.Long()));

      KafkaStreams streams = new KafkaStreams(builder.build(), props);
      streams.start();
   }
}
