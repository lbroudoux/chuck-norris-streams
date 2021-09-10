package com.github.lbroudoux.chucknorris.filter;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import io.quarkus.kafka.client.serialization.JsonbSerde;

import com.github.lbroudoux.chucknorris.filter.model.*;
import com.github.lbroudoux.chucknorris.filter.serdes.SerdeFactory;

import java.util.Properties;
/**
 * @author laurent
 */
@ApplicationScoped
public class TopologyProducer {

   @Produces
   public Topology buildTopology() {

      FilterConfig config = FilterConfig.fromEnv();
      Properties kafkaStreamsConfig = FilterConfig.getKafkaStreamsProperties(config);

      // Configure all the Serdes.
      final Serde<Integer> defaultIdSerde = SerdeFactory.createDbzEventJsonPojoSerdeFor(Integer.class, true);
      final Serde<Movie> movieSerde = SerdeFactory.createDbzEventJsonPojoSerdeFor(Movie.class, false);
      final Serde<Customer> userSerde = SerdeFactory.createDbzEventJsonPojoSerdeFor(Customer.class, false);
      final Serde<Rental> rentalSerde = SerdeFactory.createDbzEventJsonPojoSerdeFor(Rental.class, false);
      final Serde<CustomerRentalMovieAggregate> aggregateSerde = SerdeFactory.createDbzEventJsonPojoSerdeFor(CustomerRentalMovieAggregate.class, false);

      // Getting a streams builder.
      StreamsBuilder builder = new StreamsBuilder();

      // We need to make sure that all data are loaded at every restart => we need to use KTable.
      // KStream would continue where it left off, so after restart you will get an empty table.
      // The partitioning is not aligned between the different topics.
      // Both use the same number of partitions, but not the same key (rental_id versus movie_id versus customer_id)
      //    => we need to use GlobalKTable to make sure that the application will always have all the data even when use use more than one partition.
      // Since we want the GlobalKTable with the integer key, we have to use the right Serde which would decode the ID into Integer.
      GlobalKTable<Integer, Customer> usersTableInt = builder.globalTable(config.getCustomersTopic(), Consumed.with(defaultIdSerde, userSerde));
      GlobalKTable<Integer, Movie> moviesTableInt = builder.globalTable(config.getMoviesTopic(), Consumed.with(defaultIdSerde, movieSerde));

      // Create Stream for rental: we are looking at each changes excepted deletes (rental == null).
      KStream<Integer, Rental> rentalsStream = builder.stream(config.getRentalsTopic(), Consumed.with(defaultIdSerde, rentalSerde))
              .filter((rentalId, rental) -> rental != null)
              .map((rentalId, rental) -> new KeyValue<>(rental.getMovieId(), rental));
      //rentalsStream.print(Printed.toSysOut());

      // Now, let the magic happens!!
      KStream<Integer, CustomerRentalMovieAggregate> chuckNorrisRentalsStream =
            rentalsStream
                  // leftJoin would let null right sides through which is hard to handle in what we are doing here
                  // using join is better because while some rental records might be skipped, we will not get ugly null messages and NPEs
                  .join(moviesTableInt,
                          (leftKey, leftValue) -> leftKey,
                          (rental, movie) -> new CustomerRentalMovieAggregate(rental, movie))
                  .filter((movieId, urmAggregate) -> urmAggregate.getMovie().getMainActor().equals("Chuck Norris"))
                  .map((movieId, urmAggregate) -> new KeyValue<>(urmAggregate.getRental().getCustomerId(), urmAggregate))
                  // Same as above, leftJoin would let null right sides through which is hard to handle in what we are doing here
                  // using join is better because while some rental records might be skipped, we will not get ugly null messages and NPEs
                  .join(usersTableInt,
                          (leftKey, leftValue) -> leftKey,
                          (urmAggregate, customer) -> completeAggregate(urmAggregate, customer));
      chuckNorrisRentalsStream.to(config.getTargetTopic(), Produced.with(Serdes.Integer(), aggregateSerde));

      chuckNorrisRentalsStream.print(Printed.toSysOut());

      System.err.println("Starting streams!");
      return builder.build();
   }

   private static CustomerRentalMovieAggregate completeAggregate(CustomerRentalMovieAggregate urmAggregate, Customer customer) {
      System.err.println("Completing CustomerRentalMovieAggregate with " + customer.toString());
      urmAggregate.setCustomer(customer);
      return urmAggregate;
   }
}