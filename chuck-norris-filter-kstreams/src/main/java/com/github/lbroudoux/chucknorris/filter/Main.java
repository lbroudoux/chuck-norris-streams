package com.github.lbroudoux.chucknorris.filter;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;

import com.github.lbroudoux.chucknorris.filter.model.*;
import com.github.lbroudoux.chucknorris.filter.serdes.SerdeFactory;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.Properties;

/**
 * @author laurent
 */
public class Main {

   public static void main(final String[] args) {

      FilterConfig config = FilterConfig.fromEnv();
      Properties kafkaStreamsConfig = FilterConfig.getKafkaStreamsProperties(config);

      // Configure all the Serdes.
      final Serde<DefaultId> defaultIdSerde = SerdeFactory.createDbzEventJsonPojoSerdeFor(DefaultId.class, true);
      final Serde<Movie> movieSerde = SerdeFactory.createDbzEventJsonPojoSerdeFor(Movie.class, false);
      final Serde<Customer> userSerde = SerdeFactory.createDbzEventJsonPojoSerdeFor(Customer.class, false);
      final Serde<Rental> rentalSerde = SerdeFactory.createDbzEventJsonPojoSerdeFor(Rental.class, false);
      final Serde<CustomerRentalMovieAggregate> aggregateSerde = SerdeFactory.createDbzEventJsonPojoSerdeFor(CustomerRentalMovieAggregate.class, false);

      // Getting a streams builder.
      StreamsBuilder builder = new StreamsBuilder();

      // Create Tables for reference data (we only want latest status).
      KStream<Integer, Customer> usersStream = builder.stream(config.getCustomersTopic(),
            Consumed.with(defaultIdSerde, userSerde)).map((key, value) -> KeyValue.pair(value.getId(), value));
      KStream<Integer, Movie> moviesStream = builder.stream(config.getMoviesTopic(),
            Consumed.with(defaultIdSerde, movieSerde)).map((key, value) -> KeyValue.pair(value.getId(), value));


      // Deal with Integer as key so re-aggregate the tables.
      KTable<Integer, Customer> usersTableInt = usersStream.groupByKey()
            .aggregate(
               () -> null,
               (userId, customer, aggValue) -> customer,
               Materialized.<Integer, Customer, KeyValueStore<Bytes, byte[]>>as("aggregated-customers-store")
                     .withKeySerde(Serdes.Integer())
                     .withValueSerde(userSerde)
            );
      KTable<Integer, Movie> moviesTableInt = moviesStream.groupByKey()
            .aggregate(
                  () -> null,
                  (userId, movie, aggValue) -> movie,
                  Materialized.<Integer, Movie, KeyValueStore<Bytes, byte[]>>as("aggregated-movies-store")
                        .withKeySerde(Serdes.Integer())
                        .withValueSerde(movieSerde)
            );

      // Create Stream for rental: we are looking at each changes.
      KStream<DefaultId, Rental> rentalsStream = builder.stream(config.getRentalsTopic(), Consumed.with(defaultIdSerde, rentalSerde));

      // Now, let the magic happens!!
      KStream<Integer, CustomerRentalMovieAggregate> chuckNorrisRentalsStream =
            rentalsStream
                  .map((rentalId, rental) -> new KeyValue<>(rental.getMovieId(), rental))
                  .leftJoin(moviesTableInt, (rental, movie) -> new CustomerRentalMovieAggregate(rental, movie))
                  .filter((movieId, urmAggregate) -> urmAggregate.getMovie().getMainActor().equals("Chuck Norris"))
                  .leftJoin(usersTableInt, (urmAggregate, customer) -> completeAggregate(urmAggregate, customer));
      chuckNorrisRentalsStream.to(config.getTargetTopic(), Produced.with(Serdes.Integer(), aggregateSerde));

      chuckNorrisRentalsStream.print(Printed.toSysOut());

      // Building everything and run it.
      KafkaStreams streams = new KafkaStreams(builder.build(), kafkaStreamsConfig);
      streams.start();
   }

   private static CustomerRentalMovieAggregate completeAggregate(CustomerRentalMovieAggregate urmAggregate, Customer customer) {
      System.err.println("Completing CustomerRentalMovieAggregate with " + customer.toString());
      urmAggregate.setCustomer(customer);
      return urmAggregate;
   }

}