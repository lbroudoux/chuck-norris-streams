package com.github.lbroudoux.chucknorris.filter;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;

import com.github.lbroudoux.chucknorris.filter.model.*;
import com.github.lbroudoux.chucknorris.filter.serdes.SerdeFactory;

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
      KTable<DefaultId, Customer> usersTable =
            builder.table(config.getCustomersTopic(), Consumed.with(defaultIdSerde, userSerde));
      KTable<DefaultId, Movie> moviesTable =
            builder.table(config.getMoviesTopic(), Consumed.with(defaultIdSerde, movieSerde));


      // Deal with Integer as key so re-aggregate the tables.
      KTable<Integer, Customer> usersTableInt = usersTable.groupBy((key, customer) -> KeyValue.pair(customer.getId(), customer))
            .aggregate(
                  () -> null,  // Initiate the aggregate value
                  (userId, customer, aggValue) -> customer,   // adder (doing nothing, just passing the user through as the value)
                  (userId, customer, aggValue) -> customer    // subtractor (doing nothing, just passing the user through as the value)
            );
      KTable<Integer, Movie> moviesTableInt = moviesTable.groupBy((key, movie) -> KeyValue.pair(movie.getId(), movie))
            .aggregate(
                  () -> null,  // Initiate the aggregate value
                  (movieId, movie, aggValue) -> movie,   // adder (doing nothing, just passing the movie through as the value)
                  (movieId, movie, aggValue) -> movie    // subtractor (doing nothing, just passing the movie through as the value)
            );

      // Create Stream for rental: we are looking at each changes.
      KStream<DefaultId, Rental> rentalsStream = builder.stream(config.getRentalsTopic(), Consumed.with(defaultIdSerde, rentalSerde));


      // Now, let the magic happens!!
      KStream<Integer, CustomerRentalMovieAggregate> chuckNorrisRentalsStream = rentalsStream.map((key, value) -> KeyValue.pair(value.getMovieId(), value))
            .leftJoin(moviesTableInt, (rental, movie) ->
                  rental.getEventType() == EventType.DELETE ?
                        null : new CustomerRentalMovieAggregate(rental, movie))
            .filter((movieId, urmAggregate) -> urmAggregate.getMovie().getMainActor().equals("Chuck Norris"))
            .leftJoin(usersTableInt, (urmAggregate, customer) -> completeAggregate(urmAggregate, customer));

      chuckNorrisRentalsStream.to(config.getTargetTopic(), Produced.with(Serdes.Integer(), aggregateSerde));

      chuckNorrisRentalsStream.print(Printed.toSysOut());

      // Building everything and run it.
      KafkaStreams streams = new KafkaStreams(builder.build(), kafkaStreamsConfig);
      streams.start();
   }

   private static CustomerRentalMovieAggregate completeAggregate(CustomerRentalMovieAggregate urmAggregate, Customer customer) {
      urmAggregate.setCustomer(customer);
      return urmAggregate;
   }

}